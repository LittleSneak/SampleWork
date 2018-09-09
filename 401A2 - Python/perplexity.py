from log_prob import *
from preprocess import *
import os

def preplexity(LM, test_dir, language, smoothing = False, delta = 0):
    """
	Computes the preplexity of language model given a test corpus
	
	INPUT:
	
	LM : 		(dictionary) the language model trained by lm_train
	test_dir : 	(string) The top-level directory name containing data
				e.g., '/u/cs401/A2_SMT/data/Hansard/Testing/'
	language : `(string) either 'e' (English) or 'f' (French)
	smoothing : (boolean) True for add-delta smoothing, False for no smoothing
	delta : 	(float) smoothing parameter where 0<delta<=1
	"""
	
    files = os.listdir(test_dir)
    pp = 0
    N = 0
    vocab_size = len(LM["uni"])
    
    for ffile in files:
        if ffile.split(".")[-1] != language:
            continue
        
        opened_file = open(test_dir+ffile, "r")
        for line in opened_file:
            processed_line = preprocess(line, language)
            tpp = log_prob(processed_line, LM, smoothing, delta, vocab_size)
            
            if tpp > float("-inf"):
                pp = pp + tpp
                N += len(processed_line.split())
        opened_file.close()
        if N > 0:
            pp = 2**(-pp/N)
    return pp

#Testing the LM from task 2 on different values of delta
if __name__ == '__main__':
    print("English LM scores LM: ")
    with open('task2_e.pickle', 'rb') as handle:
        test_LM = pickle.load(handle)
    print("Delta = 0: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "e", smoothing = False, delta = 0))
    print("Delta = 0.1: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "e", smoothing = True, delta = 0.1))
    print("Delta = 0.3: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "e", smoothing = True, delta = 0.3))
    print("Delta = 0.5: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "e", smoothing = True, delta = 0.5))
    print("Delta = 0.9: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "e", smoothing = True, delta = 0.9))
    
    print("Testing french LM: ")
    with open('task2_f.pickle', 'rb') as handle:
        test_LM = pickle.load(handle)
    print("Delta = 0: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "f", smoothing = False, delta = 0))
    print("Delta = 0.1: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "f", smoothing = True, delta = 0.1))
    print("Delta = 0.3: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "f", smoothing = True, delta = 0.3))
    print("Delta = 0.5: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "f", smoothing = True, delta = 0.5))
    print("Delta = 0.9: ", preplexity(test_LM, "/u/cs401/A2_SMT/data/Hansard/Testing/", "f", smoothing = True, delta = 0.9))    