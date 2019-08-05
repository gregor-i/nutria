package nutria.core.languages

sealed trait XAndLambda
sealed trait ZAndLambda
sealed trait ZAndZDerAndLambda

case object X extends XAndLambda
case object Z extends ZAndLambda with ZAndZDerAndLambda
case object ZDer extends ZAndZDerAndLambda
case object Lambda extends ZAndLambda with XAndLambda with ZAndZDerAndLambda
