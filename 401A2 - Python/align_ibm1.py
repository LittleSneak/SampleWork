
from lm_train import *
from log_prob import *
from preprocess import *
from math import log
import glob
import os
import pickle

def align_ibm1(
    train_dir,
    num_sentences,
    max_iter,
    fn_AM,
    ):
    """
....Implements the training of IBM-1 word alignment algoirthm. 
....We assume that we are implemented P(foreign|english)
....
....INPUTS:
....train_dir : ....(string) The top-level directory name containing data
....................e.g., '/u/cs401/A2_SMT/data/Hansard/Testing/'
....num_sentences : (int) the maximum number of training sentences to consider
....max_iter : ........(int) the maximum number of iterations of the EM algorithm
....fn_AM : ........(string) the location to save the alignment model
....
....OUTPUT:
....AM :............(dictionary) alignment model structure
....
....The dictionary AM is a dictionary of dictionaries where AM['english_word']['foreign_word'] 
....is the computed expectation that the foreign_word is produced by english_word.
....
............LM['house']['maison'] = 0.5
...."""

    AM = {}

    # Read training data
    print("Reading sentences in...")
    eng, fre = read_hansard(train_dir, num_sentences)
    print("Reading complete")

    # Initialize AM uniformly
    print("Initializing...")
    AM = initialize(eng, fre)
    print("Initializing complete")

    # Iterate between E and M steps
    print("Iterating...")
    for count in range(0, max_iter):
        AM = em_step(AM, eng, fre)
        print("Iteration " + str(count + 1) + " complete")
    
    #Set SENTSTART -> SENTSTART and SENTEND -> SENTEND to 1
    for word in AM['SENTSTART']:
        AM['SENTSTART'][word] = 0
    AM['SENTSTART']['SENTSTART'] = 1
    for word in AM['SENTEND']:
        AM['SENTEND'][word] = 0
    AM['SENTEND']['SENTEND'] = 1
    
    # Save Model
    with open(fn_AM + '.pickle', 'wb') as handle:
        pickle.dump(AM, handle,
                    protocol=pickle.HIGHEST_PROTOCOL)
        
    return AM


# ------------ Support functions --------------

def read_hansard(train_dir, num_sentences):
    """
....Read up to num_sentences from train_dir.
....
....INPUTS:
....train_dir : ....(string) The top-level directory name containing data
....................e.g., '/u/cs401/A2_SMT/data/Hansard/Testing/'
....num_sentences : (int) the maximum number of training sentences to consider
....
....
....Make sure to preprocess!
....Remember that the i^th line in fubar.e corresponds to the i^th line in fubar.f.
....
....Make sure to read the files in an aligned manner.
...."""

    # TODO
    # MY NOTE: return two lists of sentences, one for eng and one for french

    num_read = 0
    sens_e = []
    sens_f = []
    french_files = []

    # Read num_sentences english sentences from files

    for file in glob.iglob(train_dir + '*.e'):
        french_files.append(file[:-1] + 'f')
        with open(file) as fp:
            for line in fp:
                sens_e.append(preprocess(line, 'e'))
                num_read = num_read + 1
                if num_read >= num_sentences:
                    break
            if num_read >= num_sentences:
                break

    num_read = 0

    # Read num_sentences french sentences from files
    for file in french_files:
        with open(file) as fp:
            for line in fp:
                sens_f.append(preprocess(line, 'f'))
                num_read = num_read + 1
                if num_read >= num_sentences:
                    break
            if num_read >= num_sentences:
                break
    return (sens_e, sens_f)


def initialize(eng, fre):
    """
....Initialize alignment model uniformly.
....Only set non-zero probabilities where word pairs appear in corresponding sentences.
...."""
    A_model = {}
    
    #Iterate through all english and french sentences
    index = 0
    for sent_e in eng:
        words_e = sent_e.split()
        words_f = fre[index].split()
        index = index + 1
        #Now iterate through every word pair for the sentences
        for word_e in words_e:
            #First time seeing this eng word, put in a dict for it
            if not word_e in A_model:
                A_model[word_e] = {}
            #For each english word, iterate through all french words
            for word_f in words_f:
                A_model[word_e][word_f] = 1
                    
    #Now set all the probabilities
    for word_e in A_model:
        num_pairs = len(A_model[word_e])
        for word_f in A_model[word_e]:
            A_model[word_e][word_f] = 1 / num_pairs
            
    return A_model

    # TODO

def em_step(t, eng, fre):
    """
....One step in the EM algorithm.
....Follows the pseudo-code given in the tutorial slides.
...."""
    
    index = 0
    
    tcount = {}
    total = {}
        
    #Iterate through eng fre sentence pairs
    for sent_e in eng:
        sent_f = fre[index]
        index = index + 1
        
        #Get frequencies of words
        freq_f = {}
        freq_e = {}
        words_e = sent_e.split()
        words_f = sent_f.split()
        #Frequencies for english sentence
        for word in words_e:
            if not word in freq_e:
                freq_e[word] = 1
            else:
                freq_e[word] = freq_e[word] + 1
        #Frequencies for french sentence
        for word in words_f:
            if not word in freq_f:
                freq_f[word] = 1
            else:
                freq_f[word] = freq_f[word] + 1
        #Bulk of algorithm
        for word_f in freq_f:
            denom_c = 0
            for word_e in freq_e:
                denom_c = denom_c + t[word_e][word_f] * freq_f[word_f]
            for word_e in freq_e:
                
                #Set tcount
                if(word_e not in tcount):
                    tcount[word_e] = {}
                if (word_f not in tcount[word_e]):
                    tcount[word_e][word_f] = 0
                tcount[word_e][word_f] = tcount[word_e][word_f] + t[word_e][word_f] * freq_f[word_f] * freq_e[word_e] / denom_c
                
                #Set count
                if(word_e not in total):
                    total[word_e] = 0
                total[word_e] = total[word_e] + t[word_e][word_f] * freq_f[word_f] * freq_e[word_e] / denom_c
                
    for word_e in total:
        for word_f in tcount[word_e]:
            t[word_e][word_f] = tcount[word_e][word_f] / total[word_e]
    
    return t