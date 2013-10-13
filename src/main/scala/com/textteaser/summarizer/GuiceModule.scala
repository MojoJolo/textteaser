package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import net.codingwell.scalaguice.ScalaModule
import com.google.inject._
import opennlp.tools.sentdetect._
import java.io.FileInputStream
import com.mongodb._
import org.slf4j.LoggerFactory

class GuiceModule(config: Config, dummyKeywordService: Boolean = false) extends AbstractModule with ScalaModule {
  def configure {
    bind[Config].in[Singleton]
    bind[Parser].in[Singleton]
    bind[Summarizer].in[Singleton]
    if (dummyKeywordService)
      bind[KeywordService].to[DummyKeywordService].in[Singleton]
    else
      bind[KeywordService].to[MongoKeywordService].in[Singleton]
  }

  @Provides
  def mongo = {
    val server = new ServerAddress(config.db.host, config.db.port)
    new Mongo(server)
  }

  @Provides
  @Singleton
  def sentenceDetector = {
    val model = new SentenceModel(new FileInputStream("corpus/corpusEN.bin"))
    new SentenceDetectorME(model)
  }

  @Provides
  @Singleton
  def log = LoggerFactory.getLogger("")
}