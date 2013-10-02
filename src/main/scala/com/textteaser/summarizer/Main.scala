package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import com.google.inject.Guice
import com.mongodb._
import net.liftweb.mongodb._
import com.textteaser.summarizer.models.Keyword
import com.foursquare.rogue.LiftRogue._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j._

object Main extends App {

  implicit val formats = DefaultFormats
  val config = new Config
  val guice = new ScalaInjector(Guice.createInjector(new GuiceModule(config)))

  val summarizer = guice.instance[Summarizer]
  val log = guice.instance[Logger]

  log.info("Starting...")

  MongoDB.defineDbAuth(DefaultMongoIdentifier, guice.instance[Mongo], config.db.name, config.db.username, config.db.password)

  log.info("App is now runnning.")

  val id = "anythingyoulikehere"
  val title = "Astronomic news: the universe may not be expanding after all"
  val text = "Now that conventional thinking has been turned on its head in a paper by Prof Christof Wetterich at the University of Heidelberg in Germany. He points out that the tell-tale light emitted by atoms is also governed by the masses of their constituent particles, notably their electrons. The way these absorb and emit light would shift towards the blue part of the spectrum if atoms were to grow in mass, and to the red if they lost it.  Because the frequency or “pitch” of light increases with mass, Prof Wetterich argues that masses could have been lower long ago. If they had been constantly increasing, the colours of old galaxies would look red-shifted – and the degree of red shift would depend on how far away they were from Earth. “None of my colleagues has so far found any fault [with this],” he says.  Although his research has yet to be published in a peer-reviewed publication, Nature reports that the idea that the universe is not expanding at all – or even contracting – is being taken seriously by some experts, such as Dr HongSheng Zhao, a cosmologist at the University of St Andrews who has worked on an alternative theory of gravity. “I see no fault in [Prof Wetterich’s] mathematical treatment,” he says. “There were rudimentary versions of this idea two decades ago, and I think it is fascinating to explore this alternative representation of the cosmic expansion, where the evolution of the universe is like a piano keyboard played out from low to high pitch.”  Prof Wetterich takes the detached, even playful, view that his work marks a change in perspective, with two different views of reality: either the distances between galaxies grow, as in the traditional balloon picture, or the size of atoms shrinks, increasing their mass. Or it’s a complex blend of the two. One benefit of this idea is that he is able to rid physics of the singularity at the start of time, a nasty infinity where the laws of physics break down. Instead, the Big Bang is smeared over the distant past: the first note of the ''cosmic piano’’ was long and low-pitched.  Harry Cliff, a physicist working at CERN who is the Science Museum’s fellow of modern science, thinks it striking that a universe where particles are getting heavier could look identical to one where space/time is expanding. “Finding two different ways of thinking about the same problem often leads to new insights,” he says. “String theory, for instance, is full of 'dualities’ like this, which allow theorists to pick whichever view makes their calculations simpler.”  If this idea turns out to be right – and that is a very big if – it could pave the way for new ways to think about our universe. If we are lucky, they might even be as revolutionary as Edwin Hubble’s, almost a century ago.  Roger Highfield is director of external affairs at the Science Museum"

  val article = Article(id, title, text)
  val summary = summarizer.summarize(article.article, article.title, article.id, article.blog, article.category)
  
  println(summary)
  
  log.info("Summarization completed.")
}