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

object SimpleREPL extends App {

  implicit val formats = DefaultFormats
  val config = new Config
  val guice = new ScalaInjector(Guice.createInjector(new GuiceModule(config, true)))

  val summarizer = guice.instance[Summarizer]
  val log = guice.instance[Logger]

  while(true) {
    println("Ready for summarizing:")
    println("Provide the article title:")
    val title = readLine()
    println("Provide the article text (with now newlines \\n):")
    val text = readLine()
    val article = Article("not_important", title, text)
    val summary = summarizer.summarize(article.article, article.title, article.id, article.blog, article.category)

    println(summary)

    log.info("Summarization completed.")
  }
}