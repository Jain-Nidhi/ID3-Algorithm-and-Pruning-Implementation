# ID3-Algorithm-and-Pruning-Implementation
Implementation of ID3 Algorithm and Post Pruning that predicts result for test cases.

Run the following command from command prompt to see the accuracy of ID3 Algorithm on given random test cases:
---------------------------------------------------------------------------------------------------------------
> javac ID3_Decision_and_Pruning.java
>java ID3_Decision_and_Pruning 1 training_set.csv validation_set.csv test_set.csv 1


To run the Algorithm on your own specific test cases follow the below steps:-
-------------------------------------------------------------------------------------------
1). Update the training_set.csv file with some known test cases and known results. (This will let the Algorithm learn and build the decision tree)
2). Update the validation_set.csv file with some more known test cases and known results. (This will let the Algorithm prune the previously built decision tree)
3). Update the test_set.csv file with many test cases with known results. (This will check the accuracy of the Algorithm)
4). Now run the following command: (Note the 1st Argument is the <No. of nodes to prune>. This can be changed to as many nodes you want to prune to check the accuracy of the algorithm).
    > javac ID3_Decision_and_Pruning.java
    >java ID3_Decision_and_Pruning 1 training_set.csv validation_set.csv test_set.csv 1
