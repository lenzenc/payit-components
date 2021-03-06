package com.payit.components.validation.rules

import com.payit.components.validation.{Success, Fail, Validated}

trait GeneralRules {

  type HasLength = Any { def length: Int }

  case class Required[T](allowBlank: Boolean = false) extends ValidationRule[T] {
    def apply(value: T): Validated[RuleFailure, T] = value match {
      case null => failed("required", "is required")
      case _ => if (!allowBlank && value.toString.trim.isEmpty) failed("blank", "must not be blank") else succeeded(value)
    }
  }

  case class MaxLength[T <% HasLength](max: Int) extends ValidationRule[T] {

    import scala.language.reflectiveCalls

    require(max >= 0, "max can not be less than ZERO")

    def apply(value: T): Validated[RuleFailure, T] = value match {
      case x if x != null && x.length > max => failed("maxlength", s"maximum is $max characters", Vector(max.toString))
      case _ => succeeded(value)
    }
  }

  case class MinLength[T <% HasLength](min: Int) extends ValidationRule[T] {

    import scala.language.reflectiveCalls

    require(min >= 0, "min can not be less than ZERO")

    def apply(value: T): Validated[RuleFailure, T] = value match {
      case x if x == null || (x != null && x.length < min) => failed("minlength", s"minimum is $min characters", Vector(min.toString))
      case _ => succeeded(value)
    }

  }

  case class BetweenLength[T <% HasLength](min: Int, max: Int) extends ValidationRule[T] {

    require(min >= 0, "min can not be less than ZERO")
    require(max >= 0, "max can not be less than ZERO")

    def apply(value: T): Validated[RuleFailure, T] = MinLength[T](min).apply(value) match {
      case Fail(f) => buildFailure
      case Success(_) => MaxLength[T](max).apply(value) match {
        case Fail(f) => buildFailure
        case Success(_) => succeeded(value)
      }
    }

    protected def buildFailure: Validated[RuleFailure, T] = {
      failed("betweenlength", s"should be between $min and $max characters", Vector[String](min.toString, max.toString))
    }

  }

}
