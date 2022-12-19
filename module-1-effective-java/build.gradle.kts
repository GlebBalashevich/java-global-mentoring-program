apply(plugin = "me.champeau.jmh")

configure<me.champeau.jmh.JmhParameters> {
    timeUnit.set("ms")
}


dependencies {
    implementation("com.google.guava:guava:31.1-jre")
}
