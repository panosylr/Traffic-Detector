---
Traffic-Detector
================

A Deep Packet Inspection application that provides encrypted protocol recognition based on Machine Learning

This project implements packet-based encrypted traffic classification.
In particular we are interested in analyzing traffic from six different
cases of some of the most mainstream protocols and applications. More
specifically we aim to examine Tor, SSL (web), BitTorrent PE, SSH (shell
session), SSH (SCP) and Skype. In addition in order to make the project
more challenging we assume that we want to distinguish the protocols
without having any idea about the initial handshake, which is something
that would reveal useful information about the protocol. This assumption
describes a real situation as a possible passive adversary would be
able to eavesdrop a communication anytime, without having any information
about the initial handshakes that are necessary for establishing the
connection. Moreover as we do not study the general behavior, we assume
that the study is port independent, and as a result those features
are not under consideration. In order to increase our chances of achieving
good classification accuracy, we recruited state-of-the-art classifiers
of Machine Learning. In particular we tried five classifiers such
as Support Vector Machines, MLP Neural Networks, Bayesian Networks,
C4.5 and Logistic Regression in order to evaluate their accuracy on
identifying the correct communication protocol in multiclass and binary
classifications. C4.5 algorithm proved the most
accurate classifier for our multiclass dataset. For this reason we 
implemented a system for real time encrypted traffic classification
based on the C4.5 Decision Tree and a fixed upper bound of time for traffic sampling.
---
