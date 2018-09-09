from preprocess import *
from lm_train import *
from log_prob import *
from perplexity import *
from decode import *
import pickle
from BLEU_score import *

"""
Input: iterations - The number of iterations to run each align_ibm1 on
Generates AM, and LM for 1k, 10k, 15k, 30k words then uses those to decode the
25 french sentences and prints the BLEU scores for n = 1, 2, 3
Each line printed at the end is for a sentence and each list in a printed line represents
test sizes of 1k, 10k, 15k, and 30k in that order. Within the lists are the BLEU scores
for n = 1, 2, 3 in that order
"""
def evalAlign(iterations):
    num_read = 0
    test_dir = '/u/cs401/A2_SMT/data/Hansard/Testing/'
    train_dir = '/u/cs401/A2_SMT/data/Hansard/Training/'
    
    #Training on 1k
    print("Generating LM and AM for 1k")
    print("Generating LM")
    LM1 = lm_train(train_dir, 'e', 'LM1')
    print("Generating AM")
    AM1 = align_ibm1(train_dir, 1000, iterations, 'AM1')
    
    #Training on 10k
    print("Generating LM and AM for 10k")
    print("Generating LM")
    LM2 = lm_train(train_dir, 'e', 'LM2')
    print("Generating AM")
    AM2 = align_ibm1(train_dir, 10000, iterations, 'AM2')
    
    #Training on 15k
    print("Generating LM and AM for 15k")
    print("Generating LM")
    LM3 = lm_train(train_dir, 'e', 'LM3')
    print("Generating AM")
    AM3 = align_ibm1(train_dir, 15000, iterations, 'AM3')
    
    #Training on 30k
    print("Generating LM and AM for 30k")
    print("Generating LM")
    LM4 = lm_train(train_dir, 'e', 'LM4')
    print("Generating AM")
    AM4 = align_ibm1(train_dir, 30000, iterations, 'AM4')
    
    sens_f = []
    #Read the 25 french sentences from the file
    with open(test_dir + 'Task5.f', 'r') as fp:
        for line in fp:
            sens_f.append(line)
            
            num_read = num_read + 1
            if num_read >= 25:
                break
            
    #Get 3 reference sentences for each french sentence
    #Read the 25 english reference sentences from the file
    ref_e1 = []
    num_read = 0
    with open(test_dir + 'Task5.google.e', 'r') as fp:
        for line in fp:
            ref_e1.append(preprocess(line, 'e'))
            
            num_read = num_read + 1
            if num_read >= 25:
                break
    #Read the 25 english reference sentences from the file
    ref_e2 = []
    num_read = 0
    with open(test_dir + 'Task5.e', 'r') as fp:
        for line in fp:
            ref_e2.append(preprocess(line, 'e'))
            
            num_read = num_read + 1
            if num_read >= 25:
                break
    #Read the 25 english reference sentences from the file
    ref_e3 = []
    num_read = 0
    with open(test_dir + 'Task5.e~', 'r') as fp:
        for line in fp:
            ref_e3.append(preprocess(line, 'e'))
            
            num_read = num_read + 1
            if num_read >= 25:
                break
    
    #Calculate the BLEU scores for each french sentence with different n values for 1k
    print("Calculating scores for 1k")
    scores1 = []
    for x in range(0, 25):
        candidate = decode(preprocess(sens_f[x], 'f'), LM1, AM1)
        #Reference sentences for this french sentence
        refs = []
        refs.append(ref_e1[x])
        refs.append(ref_e2[x])
        refs.append(ref_e3[x])
        
        same_sentence_scores = []
        for n in range(1, 4):
            same_sentence_scores.append(round(BLEU_score(candidate, refs, n), 4))
        scores1.append(same_sentence_scores)
        
    #Calculate the BLEU scores for each french sentence with different n values
    print("Calculating scores for 10k")
    scores2 = []
    for x in range(0, 25):
        candidate = decode(preprocess(sens_f[x], 'f'), LM2, AM2)
        #Reference sentences for this french sentence
        refs = []
        refs.append(ref_e1[x])
        refs.append(ref_e2[x])
        refs.append(ref_e3[x])
        
        same_sentence_scores = []
        for n in range(1, 4):
            same_sentence_scores.append(round(BLEU_score(candidate, refs, n), 4))
        scores2.append(same_sentence_scores)
        
    #Calculate the BLEU scores for each french sentence with different n values
    print("Calculating scores for 15k")
    scores3 = []
    for x in range(0, 25):
        candidate = decode(preprocess(sens_f[x], 'f'), LM3, AM3)
        #Reference sentences for this french sentence
        refs = []
        refs.append(ref_e1[x])
        refs.append(ref_e2[x])
        refs.append(ref_e3[x])
        
        same_sentence_scores = []
        for n in range(1, 4):
            same_sentence_scores.append(round(BLEU_score(candidate, refs, n), 4))
        scores3.append(same_sentence_scores)
    
    #Calculate the BLEU scores for each french sentence with different n values
    print("Calculating scores for 30k")
    scores4 = []
    for x in range(0, 25):
        candidate = decode(preprocess(sens_f[x], 'f'), LM4, AM4)
        #Reference sentences for this french sentence
        refs = []
        refs.append(ref_e1[x])
        refs.append(ref_e2[x])
        refs.append(ref_e3[x])
        
        same_sentence_scores = []
        for n in range(1, 4):
            same_sentence_scores.append(round(BLEU_score(candidate, refs, n), 4))
        scores4.append(same_sentence_scores)
    
    for x in range(0, 25):
        print("Sentence " + str(x + 1) + " :" , scores1[x], scores2[x], scores3[x], scores4[x])
        
#Main for getting the BLEU scores for task 5
if __name__ == '__main__':
    evalAlign(5)