package com.resume.ats.check.models
import scala.collection.mutable.Set


class RMDetail {
  private var _totalKeywords: List[String] = List.empty
  private var _unMatchedKeywords: List[String] = List.empty
  private var _matchPercentage: String = ""

  def getTotalKeywords: List[String] = _totalKeywords
  def setTotalKeywords(totalKeywords: List[String]): Unit = _totalKeywords = totalKeywords

  def getUnMatchedKeywords: List[String] = _unMatchedKeywords
  def setUnMatchedKeywords(unMatchedKeywords: List[String]): Unit = _unMatchedKeywords = unMatchedKeywords

  def getMatchPercentage: String = _matchPercentage
  def setMatchPercentage(matchPercentage: String): Unit = (_matchPercentage = "" + (matchPercentage.toDouble))

  override def toString: String = {
    //s"ATSDetail(totalKeywords=${_totalKeywords.mkString("[", ", ", "]")}, " +
     // s"unMatchedKeywords=${_unMatchedKeywords.mkString("[", ", ", "]")}, " +
      _matchPercentage
  }

}
