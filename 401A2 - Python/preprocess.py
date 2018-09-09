import re

#Words that should not be split into "d' word"
special_case_words = ["d'abord", "d'accord", "d'ailleurs", "d'habitude"]

def preprocess(in_sentence, language):
    """ 
    This function preprocesses the input text according to language-specific rules.
    Specifically, we separate contractions according to the source language, convert
    all tokens to lower-case, and separate end-of-sentence punctuation 
	
	INPUTS:
	in_sentence : (string) the original sentence to be processed
	language	: (string) either 'e' (English) or 'f' (French)
				   Language of in_sentence
				   
	OUTPUT:
	out_sentence: (string) the modified sentence
    """
    # TODO: Implement Function
    words = in_sentence.lower().split()
    out_sentence = ""
    
    new_words = []
    #Handle punctuation first
    for word in words:
        index = 0
        #Iterate through every letter in the word and add a
        #space before and after the puncutation being split
        for letter in word:
            if str(letter) in ",:;\"()+-=<>":
                word = word[:index] + " " + word[index] + " " + word[index + 1:]
                index = index + 2
            index = index + 1
        new_words.extend(word.split())
            
    words = new_words
    #Handle contractions for french
    if language == 'f':
        for word in words:
            #Check if it starts with l'
            if word.startswith("l'"):
                out_sentence = out_sentence + word[:2] + " " + word[2:] + " "
            #Check for consonant apostrophe but do not separate some words
            if len(word) > 1 and word[1] == "'" and word[0] in "bcdfghjklmnpqrstvwxz":
                #Some words we do not separate even with a consonant apostrophe
                if word not in special_case_words:
                    out_sentence = out_sentence + word[:2] + " " + word[2:] + " "
            elif(word.startswith("qu'")):
                out_sentence = out_sentence + word[:3] + " " + word[3:] + " "
            elif(word.startswith("puisqu'")):
                out_sentence = out_sentence + word[:7] + " " + word[7:] + " "
            elif(word.startswith("lorsque'")):
                out_sentence = out_sentence + word[:8] + " " + word[8:] + " "
            else:
                out_sentence = out_sentence + word + " "            
                
    #Handle english contractions
    elif language == 'e':
        for word in words:
            #Split words like won't into wo n't
            if word.endswith("n't"):
                out_sentence = out_sentence + word[:-3] + " " + word[-3:] + " "
            elif word.endswith("'ll"):
                out_sentence = out_sentence + word[:-3] + " " + word[-3:] + " "
            elif word.endswith("'ve"):
                out_sentence = out_sentence + word[:-3] + " " + word[-3:] + " "
            elif word.endswith("'s"):
                out_sentence = out_sentence + word[:-2] + " " + word[-2:] + " "
            else:
                out_sentence = out_sentence + word + " "
        
    #Handle sentence ending punctuation
    #At second last index since the last index will be " "
    if(out_sentence[-2] in ".?!"):
        out_sentence = out_sentence[:-2] + " " + out_sentence[-2]
        
    out_sentence = 'SENTSTART' + " " + out_sentence + " " + 'SENTEND'
    return out_sentence
