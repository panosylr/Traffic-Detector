package cmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/* A class that uses WEKA API to build and test the classifier of the project 
 * and also perform decision alert
 * @author Panagiotis
 * 
 */

public class WekaAnalyser implements Runnable {

	BufferedReader breader = null;
	Classifier classifier;
	Instances train;
	Instances test;
	Instances labeled;
	String myclass;
	String[] lineArray;
 

	public void decisionAlert() {
		
		List<String> tokens = new ArrayList<String>();
		tokens.add("TOR");
		tokens.add("SSLWEB");
		tokens.add("SSLp2p");
		tokens.add("SSH");
		tokens.add("SCP");
		tokens.add("SKYPE");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"labeled.arff"));
			try {
				String line = "";
				lineArray = new String[17];
				for (int i=0; i <100; i++) {

					line = reader.readLine();
					
					if ((line!=null)&&(i>=22)){
						lineArray = line.split(",");
						myclass = lineArray[17];
						

						for (String s : tokens) {

							Boolean found = myclass.equals(s);
							if (found) {
								System.out.println("");
								System.out
										.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
								System.out.print("+ The user " + System.getProperty("user.name")+" initiated " + s
										+ " traffic ");
								System.out.println("         +");
								System.out
										.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
								break;
							}

							if (s.equals("SKYPE") && (found = false)) {
								System.out.println("No pattern found");
							}

						}

					}
				//	else {
				//		System.out.println("Adeia grammi vrethike");
				//		break;
				//	}
				}
				

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void trainAndTest() {

		try {
			breader = new BufferedReader(new FileReader("TRAIN-FULL.arff")); // define
																			// the
																			// training
																			// dataset

			try {
				train = new Instances(breader); // train object with training
												// dataset assigned
				train.setClassIndex(train.numAttributes() - 1); // define where
																// the attribute
																// Class is in
																// the .arff
																// file

				breader = new BufferedReader(new FileReader("test-final.arff")); // define
																			// the
																			// test
																			// dataset
				test = new Instances(breader); // test object with test dataset
												// assigned
				test.setClassIndex(train.numAttributes() - 1); // define where
																// the class is
				breader.close();

				classifier = new J48(); // we use the C4.5 algorithm as
												// classifier

				try {
					classifier.buildClassifier(train); // train the classifier
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				labeled = new Instances(test); // this object will contain all
												// the instances after labeled

				// label instances iteration
				for (int i = 0; i < test.numInstances(); i++) {

					try {
						double clsLabel = classifier.classifyInstance(test.instance(i)); // classify "i" instances - guess
												// the class of each one of the
												// test dataset
						labeled.instance(i).setClassValue(clsLabel); // set the
																		// class
																		// by
																		// assignment
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				// save and write labeled data
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						"labeled.arff")); // writing the contents to a produced
											// file
				writer.write(labeled.toString()); // writing the class to the
													// produced file
				writer.close();

				System.out.println("");
				
				try {
					System.out.print(".");
					System.out.print(". ");
					System.out.print("Training completed successfully..");
					System.out.print("Now classifying test data");
					Thread.sleep(2000);
					System.out.print(".");
					System.out.println(". ");
					System.out.print("");
					System.out.println("");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				decisionAlert(); //

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		trainAndTest();
	}
}
