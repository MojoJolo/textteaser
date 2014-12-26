package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import com.google.inject.Guice
import com.textteaser.summarizer.models.Keyword
import org.slf4j._
import scala.io.StdIn

object SimpleREPL extends App {

  val config = new Config { override def lang = "ES" }
  val guice = new ScalaInjector(Guice.createInjector(new GuiceModule(config, true)))

  val summarizer = guice.instance[Summarizer]
  val log = guice.instance[Logger]

  while(true) {
    println("Ready for summarizing:")
    println("Provide the article title:")
    val title = StdIn.readLine()
    println("Provide the article text (with now newlines \\n):")
    val text = StdIn.readLine().replaceAll("\\\\n", "\n")
    println(text)
    val article = Article("not_important", title, text)
    val summary = summarizer.summarize(article.article, article.title, article.id, article.blog, article.category)

    println("---- Summary ----")
    summary.foreach(println)
    println("-----------------")

    log.info("Summarization completed.")
  }
}