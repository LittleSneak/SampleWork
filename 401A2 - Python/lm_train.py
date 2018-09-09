
from preprocess import *
import pickle
import os
import glob


def lm_train(data_dir, language, fn_LM):
    """
....This function reads data from data_dir, computes unigram and bigram counts,
....and writes the result to fn_LM
....
....INPUTS:
....
    data_dir....: (string) The top-level directory continaing the data from which
....................to train or decode. e.g., '/u/cs401/A2_SMT/data/Toy/'
....language....: (string) either 'e' (English) or 'f' (French)
....fn_LM........: (string) the location to save the language model once trained
    
    OUTPUT
....
....LM............: (dictionary) a specialized language model
....
....The file fn_LM must contain the data structured called "LM", which is a dictionary
....having two fields: 'uni' and 'bi', each of which holds sub-structures which 
....incorporate unigram or bigram counts
....
....e.g., LM['uni']['word'] = 5 ........# The word 'word' appears 5 times
........  LM['bi']['word']['bird'] = 2 ....# The bigram 'word bird' appears 2 times.
    """

    # TODO: Implement Function

    language_model = {}
    uni = {}
    bi = {}
    language_model['uni'] = uni
    language_model['bi'] = bi

    # Iterate through all files with given language

    for file in glob.iglob(data_dir + '*.' + language):
        with open(file) as fp:
        # Iterate through each line in file
            for line in fp:
                line = preprocess(line, language)
                words = line.split()
                prev_word = ' '
                # Iterate through every word in the sentence
                for word in words:
                    # Add word to uni dictionary if it's not there and increment otherwise
                    if word in language_model['uni']:
                        language_model['uni'][word] = \
                            language_model['uni'][word] + 1
                    else:
                        language_model['uni'][word] = 1
                        
                    # Do the same as above but for the bi dictionary
                    if prev_word in language_model['bi'] and word \
                        in language_model['bi'][prev_word]:
                        
                        language_model['bi'][prev_word][word] = \
                            language_model['bi'][prev_word][word] + 1
                        
                    #Create a new dictionary for word to count if it does not exist
                    #for the current bigram
                    elif(prev_word != " "):
                        #Word does not have dictionary yet, add one
                        if(not prev_word in language_model['bi']):
                            new_dict = {}
                            language_model['bi'][prev_word] = new_dict
                        #Check if word is in dictionary for prev_word yet
                        if(not word in language_model['bi'][prev_word]):
                            language_model['bi'][prev_word][word] = 1
                        else:
                            language_model['bi'][prev_word][word] = language_model['bi'][prev_word][word] + 1
                    prev_word = word

    # Save Model

    with open(fn_LM + '.pickle', 'wb') as handle:
        pickle.dump(language_model, handle,
                    protocol=pickle.HIGHEST_PROTOCOL)

    return language_model

#Main to generate the LM that will be tested in task 3
if __name__ == '__main__':
    lm_e = lm_train('/u/cs401/A2_SMT/data/Hansard/Training/', 'e',
                  'task2_e')
    lm_f = lm_train('/u/cs401/A2_SMT/data/Hansard/Training/', 'f',
                  'task2_f')    

