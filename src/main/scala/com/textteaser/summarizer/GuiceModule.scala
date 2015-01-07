package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import net.codingwell.scalaguice.ScalaModule
import com.google.inject._
import javax.inject.Named
import opennlp.tools.sentdetect._
import java.io.FileInputStream
import com.mongodb._
import org.slf4j.LoggerFactory

class GuiceModule(config: Config, dummyKeywordService: Boolean = false) extends AbstractModule with ScalaModule {
  def configure {
    bind[Config].toInstance(config)
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
  def sentenceDetector(@Named("lang")lang: String) = {
    val model = new SentenceModel(new FileInputStream("corpus/corpus" + lang + ".bin"))
    new SentenceDetectorME(model)
  }
  
  @Provides
  @Singleton
  @Named("lang")
  def lang(config: Config): String = config.lang

  @Provides
  @Singleton
  def log = LoggerFactory.getLogger("")
  
  @Provides
  @Singleton
  def stopWords(@Named("lang")lang: String) = StopWords.forLang(lang)
}