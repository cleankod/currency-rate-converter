package pl.cleankod

class BaseApplicationSpecification {
  static init = true

  @SuppressWarnings('unused')
  static def setupSpec() {
    if (init) {
      ApplicationInitializer.main(new String[0])
      init = false
    }
  }
}
