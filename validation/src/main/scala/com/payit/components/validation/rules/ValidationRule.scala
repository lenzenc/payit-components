package com.payit.components.validation.rules

trait ValidationRule[T] extends (T => Result) {

}
