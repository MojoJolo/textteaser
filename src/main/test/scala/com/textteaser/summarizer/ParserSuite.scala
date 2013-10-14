package com.textteaser.summarizer

import org.scalatest.{BeforeAndAfter, FunSuite}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import com.google.inject.Guice

class ParserSuite extends FunSuite with BeforeAndAfter {

  val guice = new ScalaInjector(Guice.createInjector(new GuiceModule(new Config)))
  val parser = guice.instance[Parser]

  val sentenceWithFiveWords: Array[String] = Array("1", "2", "3", "4", "5")
  val emptySentence:        Array[String] = Array()
  val sentenceWithTwentyWords: Array[String] = (1 to 20).map(_.toString).toArray


  val textBuilder         = StringBuilder.newBuilder
  val longTextBuilder     = StringBuilder.newBuilder
  val stopWordsSentence   = Array("hereafter", "hereby", "herein")
  val noStopWordsSentence = Array("Accommodation", "globalization", "emancipation")
  val title               = Array("Accommodation", "globalization", "emancipation")
  val textForKeywords     = "oneone twotwo twotwo threethree threethree threethree"

  before {
    longTextBuilder ++= "1914 translation by H. Rackham\n\n"
    longTextBuilder ++= "On the other hand, we denounce with righteous indignation and dislike men "
    longTextBuilder ++= "who are so beguiled and demoralized by the charms "
    longTextBuilder ++= "of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble "
    longTextBuilder ++= "that are bound to ensue; and equal blame belongs to those who fail in their duty "
    longTextBuilder ++= "through weakness of will, which is the same as saying through shrinking from toil and pain. "
    longTextBuilder ++= "These cases are perfectly simple and easy to distinguish. In a free hour, "
    longTextBuilder ++= "when our power of choice is untrammelled and when nothing prevents our being able to do "
    longTextBuilder ++= "what we like best, every pleasure is to be welcomed and every pain avoided. "
    longTextBuilder ++= "But in certain circumstances and owing to the claims of duty or the obligations of business "
    longTextBuilder ++= "it will frequently occur that pleasures have to be repudiated and annoyances accepted. "
    longTextBuilder ++= "The wise man therefore always holds in these matters to this principle of selection: "
    longTextBuilder ++= "he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains."

    textBuilder ++= "Now that conventional thinking has been turned on its head in a paper by "
    textBuilder ++= "Prof Christof Wetterich at the University of Heidelberg in Germany. "
    textBuilder ++= "He points out that the tell-tale light emitted by atoms is also governed by the masses "
    textBuilder ++= "of their constituent particles, notably their electrons. The way these absorb and emit "
    textBuilder ++= "light would shift towards the blue part of the spectrum if atoms were to grow in mass, "
    textBuilder ++= "and to the red if they lost it.  Because the frequency or ÒpitchÓ of light increases with mass, "
    textBuilder ++= "Prof Wetterich argues that masses could have been lower long ago. "
    textBuilder ++= "If they had been constantly increasing, the colours of old galaxies would look red-shifted Ð"
    textBuilder ++= "and the degree of red shift would depend on how far away they were from Earth. "
    textBuilder ++= "ÒNone of my colleagues has so far found any fault [with this],Ó he says.  "
    textBuilder ++= "Although his research has yet to be published in a peer-reviewed publication, Nature reports "
    textBuilder ++= "that the idea that the universe is not expanding at all Ð or even contracting Ð is being taken "
    textBuilder ++= "seriously by some experts, such as Dr HongSheng Zhao, a cosmologist at the University of "
    textBuilder ++= "St Andrews who has worked on an alternative theory of gravity. ÒI see no fault in [Prof WetterichÕs] "
    textBuilder ++= "mathematical treatment,Ó he says. ÒThere were rudimentary versions of this idea two decades ago, and "
    textBuilder ++= "I think it is fascinating to explore this alternative representation of the cosmic expansion, where the evolution"
    textBuilder ++= "of the universe is like a piano keyboard played out from low to high pitch.Ó  Prof Wetterich takes the detached,"
    textBuilder ++= " even playful, view that his work marks a change in perspective, with two different views of reality: "
    textBuilder ++= "either the distances between galaxies grow, as in the traditional balloon picture, or the size of atoms "
    textBuilder ++= "shrinks, increasing their mass. Or itÕs a complex blend of the two. One benefit of this idea"
    textBuilder ++= "is that he is able to rid physics of the singularity at the start of time, a nasty infinity where "
    textBuilder ++= "the laws of physics break down. Instead, the Big Bang is smeared over the distant past : "
    textBuilder ++= "the first note of the ''cosmic pianoÕÕ was long and low-pitched.  Harry Cliff, a physicist working at CERN"
    textBuilder ++= "who is the Science MuseumÕs fellow of modern science, thinks it striking that a universe where particles are "
    textBuilder ++= "getting heavier could look identical to one where space/time is expanding. ÒFinding two different "
    textBuilder ++= "ways of thinking about the same problem often leads to new insights,Ó he says. ÒString theory, "
    textBuilder ++= " for instance, is full of 'dualitiesÕ like this, which allow theorists to pick whichever view "
    textBuilder ++= "makes their calculations simpler.Ó  If this idea turns out to be right Ð and that is a very big "
    textBuilder ++= "if Ð it could pave the way for new ways to think about our universe. If we are lucky, they might "
    textBuilder ++= "even be as revolutionary as Edwin HubbleÕs, almost a century ago.  Roger Highfield is director "
    textBuilder ++= "of external affairs at the Science Museum"
  }

  test("Sentence length on empty sentence returns 0") {
    assert(parser.sentenceLength(emptySentence) === 0.0)
  }

  test("Sentence length on non-empty sentence returns it's length according to formula") {
    assert(parser.sentenceLength(sentenceWithFiveWords) === 0.25)
  }

  test("When `ideal` is equal to `sentence` array length, sentence length should be 1") {
    assert(parser.sentenceLength(sentenceWithTwentyWords) === 1.0)
  }

  test("Splitting string into words should return no empty strings") {
    assert(!parser.splitWords(longTextBuilder.toString()).contains(""))
    assert(!parser.splitWords(textBuilder.toString()).contains(""))
  }

  test("Splitting string into words should not produce whitespaces in output") {
    assert(parser.splitWords(longTextBuilder.toString()).forall(s => """\s+""".r.findFirstIn(s) == None))
    assert(parser.splitWords(textBuilder.toString()).forall(s => """\s+""".r.findFirstIn(s) == None))
  }

  test("Splitting string into words should not produce newlines in output") {
    assert(parser.splitWords(longTextBuilder.toString()).forall(s => """\r?\n+""".r.findFirstIn(s) == None))
    assert(parser.splitWords(textBuilder.toString()).forall(s => """\r?\n+""".r.findFirstIn(s) == None))
  }

  test("Splitting string into words should let digits and letters pass") {
    assert(parser.splitWords(longTextBuilder.toString()).forall(s => s matches """\w+"""))
    assert(parser.splitWords(textBuilder.toString()).forall(s => s matches """\w+"""))
  }

  test("Title score of sentence consisting solely of stop words should be 0") {
    assert(parser.titleScore(title, stopWordsSentence) === 0.0)
  }

  test("Title score of sentence that hasn't stop words should be 1") {
    assert(parser.titleScore(title, noStopWordsSentence) === 1.0)
  }

  test("Keywords are sorted in descending order") {
    assert(parser.getKeywords(textForKeywords) ===
      KeywordList(List(ArticleKeyword("threethree", 3), ArticleKeyword("twotwo", 2), ArticleKeyword("oneone", 1)), 6))
  }

  test("Keywords are unique") {
    assert(parser.getKeywords(textForKeywords).keywords.toSet ===
      Set(ArticleKeyword("threethree", 3), ArticleKeyword("twotwo", 2), ArticleKeyword("oneone", 1)))
  }

  test("Any keyword isn't present in stopWords list") {
    assert(parser.getKeywords(textForKeywords).keywords.forall(aw => !parser.stopWords.contains(aw.word)))
  }


}
