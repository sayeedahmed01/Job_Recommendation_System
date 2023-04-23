package com.resume.ats.check.service

import java.io.{FileInputStream, IOException, InputStream}
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import opennlp.tools.postag.{POSModel, POSTaggerME}
import opennlp.tools.tokenize.{Tokenizer, TokenizerME, TokenizerModel}
import scala.collection.mutable.ListBuffer


@Service
class KeywordExtractorService {

  private val TOKEN_MODEL_PATH = "src/main/resources/en-token.bin"
  private val POS_MODEL_PATH = "src/main/resources/en-pos-maxent.bin"
  private val STOPWORDS_PATH = "src/main/resources/stopwords.txt"

  private var tokenizer: Tokenizer = _
  private var posTagger: POSTaggerME = _
  private var stopWords: List[String] = _

  @PostConstruct
  @throws[IOException]
  def init(): Unit = {
    tokenizer = loadTokenizer()
    posTagger = loadPOSTagger()
    stopWords = loadStopWords()
  }

  @throws[IOException]
  def extractKeywords(inputText: String): List[String] = {
    // Tokenize input text
    val tokens: Array[String] = tokenizeInputText(inputText, tokenizer)

    // Tag parts of speech in input text
    val posTags: Array[String] = tagPartsOfSpeech(tokens, posTagger)

    // Extract keywords based on relevant parts of speech
    extractKeywordsFromPartsOfSpeech(tokens, posTags, stopWords)
  }

  @throws[IOException]
  private def loadTokenizer(): Tokenizer = {
    val tokenModelIn: InputStream = new FileInputStream(TOKEN_MODEL_PATH)
    val tokenModel: TokenizerModel = new TokenizerModel(tokenModelIn)
    new TokenizerME(tokenModel)
  }

  @throws[IOException]
  private def loadPOSTagger(): POSTaggerME = {
    val posModelIn: InputStream = new FileInputStream(POS_MODEL_PATH)
    val posModel: POSModel = new POSModel(posModelIn)
    new POSTaggerME(posModel)
  }

  @throws[IOException]
  private def loadStopWords(): List[String] = {
    val stopWordsIn: InputStream = new FileInputStream(STOPWORDS_PATH)
    IOUtils.readLines(stopWordsIn, StandardCharsets.UTF_8).toArray.toList.asInstanceOf[List[String]]
  }

  private def tokenizeInputText(inputText: String, tokenizer: Tokenizer): Array[String] = {
    tokenizer.tokenize(inputText)
  }

  private def tagPartsOfSpeech(tokens: Array[String], posTagger: POSTaggerME): Array[String] = {
    posTagger.tag(tokens)
  }

  private def extractKeywordsFromPartsOfSpeech(tokens: Array[String], posTags: Array[String], stopWords: List[String]): List[String] = {
    val keywords = new ListBuffer[String]()

    for (i <- tokens.indices) {
      var token = tokens(i)
      val posTag = posTags(i)
      if (posTag.startsWith("N") || posTag.startsWith("J")) {
        // Consider only nouns and adjectives
        if (token.matches(".*\\[.*\\].*")) {
          // Remove any parts-of-speech tags from token
          token = token.replaceAll("\\[.*\\]", "")
        }
        if (token.contains("/")) {
          val wordsSplit = token.split("/")
          keywords ++= wordsSplit
        } else {
          token = token.replaceAll("[^a-zA-Z0-9]", " ").trim.toLowerCase
          val words = token.split(" ").filter(word => !stopWords.contains(word))
          keywords ++= words
        }
      }
    }

    keywords.toList
  }
}
