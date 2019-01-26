Coding challenge for a position with Parlamind implemented in Java. 

Token importance is defiend based on precomputed tfidf values of tokens in the corpus. Important tokens are then tokens with tfidf values greater tan mean(tfidf) of all tokens (cca 2.5K tokens).  

Important sentences are calculated in two ways:
1. based on tfidf values of tokens in corus, using all tokens. Important senteces are give with sum[tfidf] of sentence tokens > mean(all sentences in corpus)
2. based on tfidf values of most importance tokens in corpus. 

To run 
path\to\java -Dfile.encoding=windows-1252 -jar path\to\jar\parlamind.jar

I.e. on Windows, on my local machine:
"C:\Program Files\Java\jdk1.8.0_92\bin\java.exe" -Dfile.encoding=windows-1252 -jar C:\Users\sevajuri\IdeaProjects\parlamind\out\artifacts\parlamind_jar\parlamind.jar