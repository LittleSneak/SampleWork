#!/usr/bin/python
# -*- coding: utf-8 -*-
import math


def BLEU_score(candidate, references, n):
    """
....Compute the LOG probability of a sentence, given a language model and whether or not to
....apply add-delta smoothing
....
....INPUTS:
....sentence :....(string) Candidate sentence.  "SENTSTART i am hungry SENTEND"
....references:....(list) List containing reference sentences. ["SENTSTART je suis faim SENTEND", "SENTSTART nous sommes faime SENTEND"]
....n :............(int) one of 1,2,3. N-Gram level.

....
....OUTPUT:
....bleu_score :....(float) The BLEU score
...."""

    # TODO: Implement by student.

    words = candidate.split()

    # Find p1
    # Check how many words appear in at least one reference

    p1 = 0
    for word in words:
        for ref_sen in references:
            if word in ref_sen:
                p1 = p1 + 1
                break
    p1 = p1 / len(words)

    # Repeat for bigrams
    # Ignore p2 if the sentence is too short or if n is 1

    num_bigrams = 0
    if n == 1 or len(words) == 1:
        p2 = 1
        num_bigrams = 1
    else:
        p2 = 0
        for index in range(1, len(words)):
            num_bigrams = num_bigrams + 1
            prev_word = words[index - 1]
            curr_word = words[index]
            index = index + 1
            bigram = prev_word + ' ' + curr_word

        # Check if bigram is in a reference

            for ref_sen in references:
                if bigram in ref_sen:
                    p2 = p2 + 1
                    break
    p2 = p2 / num_bigrams

    # Repeat for trigrams

    num_trigrams = 0
    if n <= 2 or len(words) <= 2:
        p3 = 1
        num_trigrams = 1
    else:
        p3 = 0
        for index in range(2, len(words)):
            num_trigrams = num_trigrams + 1
            prev_prev_word = words[index - 2]
            prev_word = words[index - 1]
            curr_word = words[index]
            index = index + 1
            trigram = prev_prev_word + ' ' + prev_word + ' ' + curr_word

        # Check if bigram is in a reference

            for ref_sen in references:
                if trigram in ref_sen:
                    p3 = p3 + 1
                    break
    p3 = p3 / num_trigrams

    # Find the ref that's closest in length

    candidate_len = len(words)
    ref_len = len(references[0].split())
    min_diff = math.fabs(candidate_len - ref_len)
    for ref_sen in references:
        new_ref_len = len(ref_sen.split())
        new_diff = math.fabs(ref_len - new_ref_len)
        if new_diff < min_diff:
            ref_len = new_ref_len
            min_diff = new_diff

    # Get the bp

    brevity = ref_len / candidate_len
    if brevity < 1:
        bp = 1
    else:
        bp = math.e ** (1 - brevity)

    """print ('cand len: ', candidate_len)
    print ('ref len: ', ref_len)
    print ('p1: ', p1)
    print ('p2: ', p2)
    print ('p3: ', p3)
    print ('bp: ', bp)"""
    bleu_score = bp * (p1 * p2 * p3) ** (1 / n)
    return bleu_score