# morse-nrepl

The start of an nREPL middleware that will spy on an [nREPL][gh-nrepl] connection
and capture the results of evaluation for browsing in [morse][gh-morse].

## Rationale

[morse][gh-morse] was designed to be used alongside [Replicant][gh-replicant], and while this is a great approach
and a clear step forward, the integration is not seamless.
The transition from a setup using [REBL][gh-rebl] and [nrebl.middleware][gh-nrebl.middleware] to one that incorporates morse and Replicant involves a minor, yet necessary, setup time.

Essentially, this project serves as an adaptation of [nrebl.middleware][gh-nrebl.middleware] for morse,
aiming to ease the transition from REBL to morse.

[gh-nrebl.middleware]: https://github.com/rynkowsg/nrebl.middleware
[gh-nrepl]: https://github.com/nrepl/nrepl
[gh-morse]: https://github.com/nubank/morse
[gh-rebl]: https://github.com/cognitect-labs/REBL-distro
[gh-replicant]: https://github.com/clojure/data.alpha.replicant-server

## Usage

In this setup, similarly to REBL+nrepl, we assume you run morse within the nREPL process.
When you start the process you can apply one of two middlewares:
- `pl.rynkowski.morse-nrepl/wrap` - all the forms sent to nREPL server are sent to morse,
- `pl.rynkowski.morse-nrepl/launch-and-wrap` - same as above plus the launch morse on process start.

If you apply `pl.rynkowski.morse-nrepl/wrap`, you need to launch morse
with `(dev.nu.morse/launch-in-proc)` if you want to make any use of the middleware.

The only forms that are not sent to morse are Cursive's forms that are filtered out.

### Dependency

```clojure
pl.rynkowski/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                          :git/branch "master"
                          :git/sha "acfc3293f2c3869ddfa7b39e414f5638923fabc7"}
```

### Example aliases

```clojure
;; launch nrepl with middleware applied, then open morse with `(dev.nu.morse/launch-in-proc)`.
:nrepl-morse
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.10.06.02" :git/sha "88b5ff7"}
              pl.rynkowski/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                        :git/branch "master"
                                        :git/sha "acfc3293f2c3869ddfa7b39e414f5638923fabc7"}
              nrepl/nrepl {:mvn/version "1.3.0"}}
 :main-opts  ["-m" "nrepl.cmdline" "-i" "--middleware" "[pl.rynkowski.morse-nrepl/wrap]"]}

;; launch nrepl and morse
:nrepl-morse-on-start
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.10.06.02" :git/sha "88b5ff7"}
              pl.rynkowski/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                        :git/branch "master"
                                        :git/sha "acfc3293f2c3869ddfa7b39e414f5638923fabc7"}
              nrepl/nrepl {:mvn/version "1.3.0"}}
 :main-opts  ["-m" "nrepl.cmdline" "-i" "--middleware" "[pl.rynkowski.morse-nrepl/launch-and-wrap]"]}}
```

## More

- https://github.com/nubank/morse

## License

Copyright © 2024 Grzegorz Rynkowski

Released under the MIT license.
