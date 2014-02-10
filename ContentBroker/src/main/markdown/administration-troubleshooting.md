As you have already read in the administration.md (if you don't have, do it first),
the WorkArea is the space where the ContentBroker manipulates the contents of objects. In a step by step
transition, it transforms contents of objects. Depending on the workflow, that means adding data
(by conversion or adding metadata), or removing of data (for DIPs or presentation). In any case the main unit
of operation is an unpacked object which comes either from the IngestArea or the LZAArea or both (in case of deltas).
To achieve the goal of transforming an object into a desired final state, the object has to go through one of
the possible workflows of the ContentBroker. Any Workflow consists of small, well defined steps.








### Error handling

## Error state 361 - PrepareSendToPresenterAction

* this one can easily be rolled back if you see the Dear admin message at log/object-logs/[oid].log

## Error state 381 - tar action

* this one can easily be rolled back if you see the Dear admin message at log/object-logs/[oid].log
