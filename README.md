# Cache control in a Play 2.3 (Scala) application

## Problem 1: Appropriate result cache duration varies
Solution: Set the _Expires_ header in Action code, and determine cache duration as a function of ResponseHeader (see Application.cacheDurationFromExpiresHeader).

## Problem 2: We need _Cache-Control: max-age_ but Cached doesn't set it
Solution: Wrap Cached in another EssentialAction that adds the _Cache-Control_ header and current _max-age_ to the cached result (see Application.WithCacheControl).
