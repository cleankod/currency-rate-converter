package pl.cleankod

abstract class BaseApplicationSpecification {
  static init = true

  @SuppressWarnings('unused')
  static def setupSpec() {
    if (init) {
      ApplicationInitializer.main(new String[0])
      init = false
    }
  }
}
