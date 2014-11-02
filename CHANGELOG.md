## 0.40:
* Fix bug in AbstractSharedTest where getModules() called getDefaultTestModule()
  rather than getTestModule().

## 0.39:
* Extract AbstractSharedTest from AbstractServerTest to allow reuse of the test
  infrastructure in client-side (i.e. GWT) and non-JEE frameworks.
* Move AbstractModule to the shared package but create a deprecated old AbstractModule
  that extends the new AbstractModule.

## 0.38:
* Support multiple databases with the framework. Use prefixes to system settings to
  configure the non-primary database.

## 0.37:
* Ensure that the @Resource injections will use value of lookup parameter as the name
  if not null.

## 0.36:
* Tighten up the types on InjectUtil.toObject( type, object ).
* Add DbCleaner.isTransactionActive() and DbCleaner.isCleanScheduled() utility methods.

## 0.35:
* Add helper method to GlassFishContainer to create custom boolean resources.
* Avoid calling EntityTransaction.flush() in AbstractServerTest.flush() method when transaction is not active.

## 0.34:
* Simplify testing with mock persistence contexts, by null checking in the AbstractServerTest.flush() method.

## 0.33:
* Ensure @javax.enterprise.context.Dependent is a scope guice supports.
* Improve exception throw from PersistenceTestModule.requestInjectionForEntityListener when unable to find model in session.

## 0.32:
* Rework GlassFishContainer to raise an exception when the command is anything less than success.

## 0.31:
* Support different glassfish container versions in GlassFishContainer. Default to 4.0.

## 0.30:
* Move to eclipselink 2.5.1.

## 0.29:

* Remove support for recently added "embedded.glassfish.specs" system setting in as the implementation is unlikely
  to be usable.
* Add utility method GlassFishContainer.addSpecToClasspath() that can add maven style dependencies to classpath when
  creating GlassFish.
* Add guard against adding file that does not exist to GlassFishContainer's classpath.
* Add an extra constructor to GlassFishContainer for usability purposes.

## 0.28:

* Initial public release.