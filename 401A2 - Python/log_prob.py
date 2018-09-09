from preprocess import *
from lm_train import *
import math
import pickle

def log_prob(sentence, LM, smoothing=False, delta=0, vocabSize=0):
    """
	Compute the LOG probability of a sentence, given a language model and whether or not to
	apply add-delta smoothing
	
	INPUTS:
	sentence :	(string) The PROCESSED sentence whose probability we wish to compute
	LM :		(dictionary) The LM structure (not the filename)
	smoothing : (boolean) True for add-delta smoothing, False for no smoothing
	delta : 	(float) smoothing parameter where 0<delta<=1
	vocabSize :	(int) the number of words in the vocabulary
	
	OUTPUT:
	log_prob :	(float) log probability of sentence
	"""
    
    words = sentence.split()
    #Check input and make sure it's correct
    if smoothing and (delta < 0 or delta > 1):
        print("Error, delta must > 0 and < 1")
        return
    elif smoothing == False:
        delta = 0
        vocabSize = 0
        
    prev_word = words[0]
    probs = []
    #Iterate through the words to get the probabailities
    for index in range(1, len(words)):
        word = words[index]
        
        #Calculate numerator and denominator based on if the words are
        #in the dictionary or not
        if(prev_word in LM["uni"]):
            denominator = LM["uni"][prev_word] + delta * vocabSize
        else:
            denominator = delta * vocabSize
        if(prev_word in LM["bi"] and word in LM["bi"][prev_word]):
            numerator = LM["bi"][prev_word][word] + delta
        else:
            numerator = delta
        
        #Make sure we don't divide by 0
        if(denominator > 0):
            prob = numerator / denominator
            
        #Return -infinity if we have 0/0 probability
        else:
            return float('-inf')
        if(prob == 0):
            return float('-inf')
        probs.append(prob)
        prev_word = word
        
    #Get the log_prob
    log_prob = 0
    for prob in probs:
        log_prob = log_prob + math.log2(prob)
    return log_prob