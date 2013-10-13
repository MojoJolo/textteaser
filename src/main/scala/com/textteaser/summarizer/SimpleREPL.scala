package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import com.google.inject.Guice
import com.textteaser.summarizer.models.Keyword
import org.slf4j._

object SimpleREPL extends App {

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

    println("---- Summary ----")
    summary.foreach(println)
    println("-----------------")

    log.info("Summarization completed.")
  }
}