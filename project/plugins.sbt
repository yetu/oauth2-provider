
//TODO: remove unnecessary resolvers

resolvers += Classpaths.sbtPluginReleases

// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe repo" at "http://repo.typesafe.com/typesafe/repo/"

resolvers += Resolver.typesafeIvyRepo("releases")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.5")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0.BETA1")

addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.13")


//TODO: Change the dependency once pull request is merged:
//https://github.com/sbt/sbt-buildinfo/pull/45
//addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.5")
addSbtPlugin("com.yetu" % "sbt-buildinfo" % "0.3.5")


addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")


addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.13")

// Use the Scalariform plugin to reformat the code
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Web plugins for using Assets.at() to prevent cache
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")
