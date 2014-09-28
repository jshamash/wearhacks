package com.team38.wearhacks.tools

object Tools {
  
  /**
   * Convert a case class to a map from field name to field value
   */
  def toMap(cc: AnyRef) = 
    (Map[String, AnyRef]() /: cc.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
  }
}