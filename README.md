# Ravine

[![Actions Status](https://github.com/djjonno/elkd/workflows/Java%20CI/badge.svg)](https://github.com/djjonno/elkd/actions)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg)](https://github.com/djjonno/elkd/blob/master/LICENSE)

**Ravine** is a distributed event-streaming server built for the modern day.  It is robust, lightweight and super fast!
- **Produce/Consume** - create Topics, then produce and consume them.
- **Process** - create processors that transform or aggregate event data, and project new Streams with the outputs.
- **Distribute** - scale & balance your event processors across your cluster.

## Getting Started

### Prerequisites

* **JDK 8** or above.

### Installing

Using the gradle wrapper, you can now run.

```bash
./gradlew installDist
```

This will produce a bin file `ravine` under `./core/build/install/ravine/bin`

## Running Tests

### Unit Tests

You can run unit tests as part of a build:

```bash
./gradlew build
```

Or, you can run them specifically:

```bash
./gradlew test [--tests <some-package>|<regex>|<class-name>]
```

It is just vanilla Gradle so you can refer to the docs for more usages.  If you use Jetbrains IDEA, you can also run the tests within the editor.

## Coding Style

Kotlin is our core language and we currently enforce styling via `ktlint`.  `ktlint` will run automatically on a build task.

Andz can also run it directly:

```bash
./gradlew ktlint
```

And have the formatting done for you:

```bash
./gradlew ktlintFormat
```

## Built With

* [Kotlin](https://kotlinlang.org/)
* [gRPC](https://grpc.io/docs/quickstart/java/) - Server Communication
* [Raft Consensus](https://raft.github.io) - Distributed Consensus

## Contributing

Get on board by reading [CONTRIBUTING.md]() guidelines for details on our code of conduct, and the process for submitting pull requests!

## Versioning

We use [SemVer](https://semver.org).  For the versions available, see the [tags on this repository](https://github.com/djjonno/ravine/tags). 

## Authors

* [djjonno@](https://github.com/djjonno)
* +you – [come build something cool](CONTRIBUTING.md)

## License

This project is licensed under the MIT License - see [LICENSE.md](LICENSE.md) file for details.

## Use Cases

There are numerous uses for event-streaming, here are just a few thing you could use **Ravine** for:

- Define a Topic of UI Events (PageView, PageScroll, PageSwipe, ButtonClick, TextInput, etc) to analyze customer interaction with your product, to gain insights and optimize your critical customer flows.
- Perform time-series analysis on all kinds of application event streams.
- Create app/system audit streams for retrospective analysis for fraud or unauthorized access use-cases.
- Natively supports [CQRS](https://martinfowler.com/bliki/CQRS.html) Architectures

## Other things to checkout

- [Release Roadmap](RELEASES.md)
- [Docs]() - Coming Soon

---

*a project by [@djjonno](https://github.com/djjonno)*
