package module

import javax.inject.Provider

trait ProviderSyntax {
  implicit class EnrichProvider[A](provider: Provider[A]) {
    def flatMap[B](f: A => Provider[B]): Provider[B] = () => f(provider.get()).get()
    def map[B](f: A => B): Provider[B]               = () => f(provider.get())
  }
}
