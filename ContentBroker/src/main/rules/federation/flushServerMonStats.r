acFlushStat {
       delay("<EF>10m</EF>") {
               msiFlushMonStat("24","serverload");
               msiFlushMonStat("24","serverloaddigest");
       }
}
input null
output ruleExecOut
