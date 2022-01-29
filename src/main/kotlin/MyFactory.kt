import picocli.CommandLine

class MyFactory: CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            if (cls == Post::class.java) {
                return Post(PostsRepository()) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}