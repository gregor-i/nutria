package nutria.core.languages

sealed trait ZAndLambda
sealed trait ZAndZDerAndLambda

case object Z      extends ZAndLambda with ZAndZDerAndLambda
case object ZDer   extends ZAndZDerAndLambda
case object Lambda extends ZAndLambda with ZAndZDerAndLambda
