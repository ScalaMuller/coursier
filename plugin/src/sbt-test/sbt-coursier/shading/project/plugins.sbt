{
  val pluginVersion = sys.props.getOrElse(
    "plugin.version",
    throw new RuntimeException(
      """|The system property 'plugin.version' is not defined.
         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin
    )
  )

  addSbtPlugin("io.get-coursier" % "sbt-coursier" % pluginVersion)
}

// for the locally publish jarjar
resolvers += Resolver.mavenLocal

def coursierJarjarFoundInM2 =
  (file(sys.props("user.home")) / s".m2/repository/org/anarres/jarjar/jarjar-core/$coursierJarjarVersion").exists()

def jarjarVersion =
  if (coursierJarjarFoundInM2)
    "1.0.1-coursier-SNAPSHOT"
  else
    sys.error(
      "Ad hoc jarjar version not found. Run\n" +
      "  git clone https://github.com/alexarchambault/jarjar.git && cd jarjar && git checkout 249c8dbb970f8 && ./gradlew install\n" +
      "to run this test"
    )

libraryDependencies += "org.anarres.jarjar" % "jarjar-core" % jarjarVersion
