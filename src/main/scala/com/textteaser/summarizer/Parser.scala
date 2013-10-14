package com.textteaser.summarizer

import opennlp.tools.sentdetect._
import com.google.inject.Inject
import com.google.common.base.{CharMatcher, Splitter}
import scala.collection.JavaConverters

class Parser @Inject() (sentenceDetector: SentenceDetectorME, config: Config) {
  lazy val stopWords = Set("-", " ", ",", ".", "a", "e", "i", "o", "u", "t", "about", "above", "above", "across",
    "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although",
    "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone",
    "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become",
    "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between",
    "beyond", "both", "bottom", "but", "by", "call", "can", "cannot", "can't", "co", "con", "could", "couldn't", "de",
    "describe", "detail", "did", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven",
    "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere",
    "except", "few", "fifteen", "fifty", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty",
    "found", "four", "from", "front", "full", "further", "get", "give", "go", "got", "had", "has", "hasnt", "have",
    "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself",
    "his", "how", "however", "hundred", "i", "ie", "if", "in", "inc", "indeed", "into", "is", "it", "its", "it's",
    "itself", "just", "keep", "last", "latter", "latterly", "least", "less", "like", "ltd", "made", "make", "many",
    "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
    "my", "myself", "name", "namely", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody",
    "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only",
    "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "people",
    "per", "perhaps", "please", "put", "rather", "re", "said", "same", "see", "seem", "seemed", "seeming", "seems",
    "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone",
    "something", "sometime", "sometimes", "somewhere", "still", "such", "take", "ten", "than", "that", "the", "their",
    "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon",
    "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru",
    "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until",
    "up", "upon", "us", "use", "very", "via", "want", "was", "we", "well", "were", "what", "whatever", "when",
    "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
    "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within",
    "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the", "reuters", "news", "monday",
    "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "mon", "tue", "wed", "thu", "fri", "sat",
    "sun", "rappler", "rapplercom", "inquirer", "yahoo", "home", "sports", "1", "10", "2012", "sa", "says", "tweet",
    "pm", "home", "homepage", "sports", "section", "newsinfo", "stories", "story", "photo", "2013", "na", "ng", "ang",
    "year", "years", "percent", "ko", "ako", "yung", "yun", "2", "3", "4", "5", "6", "7", "8", "9", "0", "time",
    "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november",
    "december", "philippine", "government", "police", "manila")

  val ideal = config.words.ideal

  /*
   * Sentence Length: Computed using this formula
   * (ideal - Math.abs(ideal - words.size)) / ideal
   */
  def sentenceLength(sentence: Array[String]) = 1 - (Math.abs(ideal - sentence.size) / ideal.toDouble)

  /*
   * Split Words: Split words via white space and new lines. Then remove whites space in the resulting array.
   */
  def splitWords(source: String) = JavaConverters.iterableAsScalaIterableConverter(
    Splitter.on("""[^\w]""".r.pattern)
    .trimResults().omitEmptyStrings()
    .split(source)).asScala.toArray

  def titleScore(titleWords: Array[String], sentence: Array[String]) =
    sentence.count(w => !stopWords.contains(w) && titleWords.contains(w)) / titleWords.size.toDouble

  def getKeywords(text: String): KeywordList = {
    val keyWords = splitWords(text)
    val sizeWithRepeatingWords = keyWords.length
    KeywordList(
      keyWords.filterNot(w => stopWords.contains(w))
      .groupBy(w => w)
      .map(w => ArticleKeyword(w._1, w._2.length))
      .toList.sortBy(-_.count),
      sizeWithRepeatingWords)
  }

  def splitSentences(source: String) = sentenceDetector.sentDetect(source)

  def sentencePosition(ctr: Int, sentenceCount: Double) = {
    val normalized = ctr / sentenceCount

    if (normalized > 0 && normalized <= 0.1)
      0.17
    else if (normalized > 0.1 && normalized <= 0.2)
      0.23
    else if (normalized > 0.2 && normalized <= 0.3)
      0.14
    else if (normalized > 0.3 && normalized <= 0.4)
      0.08
    else if (normalized > 0.4 && normalized <= 0.5)
      0.05
    else if (normalized > 0.5 && normalized <= 0.6)
      0.04
    else if (normalized > 0.6 && normalized <= 0.7)
      0.06
    else if (normalized > 0.7 && normalized <= 0.8)
      0.04
    else if (normalized > 0.8 && normalized <= 0.9)
      0.04
    else if (normalized > 0.9 && normalized <= 1.0)
      0.15
    else
      0d
  }
}

case class ArticleKeyword(word: String, count: Int)
case class KeywordList(keywords: List[ArticleKeyword], wordCount: Int)