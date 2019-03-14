# elkd 🦌
[![Build Status](https://travis-ci.com/elkd/elkd.svg?branch=master)](https://travis-ci.com/elkd/elkd)
[![Releases](https://img.shields.io/github/release/elkd/elkd/all.svg?style=flat-square)](https://github.com/elkd/elkd/releases)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/mockito/mockito/blob/master/LICENSE)


[![codecov](https://codecov.io/gh/elkd/elkd/branch/master/graph/badge.svg)](https://codecov.io/gh/elkd/elkd)
[![Maintainability](https://api.codeclimate.com/v1/badges/584249e219d2df7bb0ae/maintainability)](https://codeclimate.com/github/elkd/elkd/maintainability)

elkd is a distributed and reliable store for time-series data and machine-learning analysis.  We emphasise on being:
- *Simple*: powerful, minimalistic, extensive query language
- *Secure*: TLS + Certificates
- *Fast*: 10,000 writes/sec
- *Robust*: raft distribution consensus protocol 

### Features

- Store time-series data in streams.
- Expressive but minimal query language:
    - Analogous to traditional programming languages - opting for familiarity
    - Programmatic access in Kotlin/Java (no SQL statements)
    - Extensible with elkd plugins
- Static cluster configuration
    - Will be dynamic in future release

*elkd is under active development and features are in flux.*

### Schedule for 0.1 (first release)
- Fully distributed and reliable key/value store
    - elkd-core consensus & replicated log stabilized
    - eventually consistent
- Simple query language exposed 
	- `set [key] [value]`
	- `get [key]`
	- `rm [key]`
- Java/Kotlin client
