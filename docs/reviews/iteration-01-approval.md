# Planning review and approval

Status: PENDING. No owner approval is asserted by this package.

Review these proposed choices before T01:

- [ ] Local synthetic-data laboratory; no real processor or public endpoint.
- [ ] Java 17 baseline; compatible versions verified and pinned during T01.
- [ ] CAD only; positive bounded integer minor units; two allowed synthetic aliases.
- [ ] Replays preserve identity and return current state (not byte-identical responses).
- [ ] Database uniqueness now; 20-request concurrency proof in Iteration 2.
- [ ] No blind retries; UNKNOWN retained; crash-stranded PROCESSING recovery deferred.
- [ ] Simulator state is volatile; payment state survives payment-service restart.
- [ ] Full Docker evidence required before declaring Iteration 1 complete.
- [ ] Remote deployment blocked pending authentication and tenant isolation.
- [ ] One task at a time, with owner review and honest evidence.

Approval record (fill only after actual approval):

Owner:
Date:
Approved revision:
Changes requested / accepted exceptions:
First authorized task:

## Plan challenge

The main deliberate gap is liveness after crashes: correctness favors avoiding duplicate submission over automatically completing every payment. The main security limitation is unauthenticated local identity. The main verification risk is skipped Docker tests. These limitations are documented in the spec rather than silently treated as solved.

Teach-back: Why is a timeout not a decline? What happens if the service crashes immediately before or after calling the processor? Why are both database uniqueness and processor idempotency needed? What evidence proves a response was lost after approval?
