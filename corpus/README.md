Training new models
===================

This folder contains Maximum Entropy models for sentence splitting, as needed by OpenNLP 1.5.

See http://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.sentdetect.training

For more models, see: http://opennlp.sourceforge.net/models-1.5/

Migrating from NLTK
-------------------

<pre>
$ pip install nltk
$ python
>>> import nltk
>>> nltk.download()
</pre>

_install punkt_

<pre>
>>> import nltk.data
>>> sent_detector = nltk.data.load('tokenizers/punkt/spanish.pickle')
>>> import codecs
>>> text=codecs.open("/path/to/corpus-utf8.txt","r","utf-8").read()
>>> sents=sent_detector.tokenize(text)
>>> w=codecs.open("train.txt","w","utf-8")
>>>  for s in sents:
...   w.write(s + '\n')
...   c += 1
...   if c == 10:
...     w.write('\n')
...     c = 0
>>> w.close()
>>> 
$ opennlp SentenceDetectorTrainer -model corpusES.bin -lang es -data train.txt -encoding UTF-8
</pre>



