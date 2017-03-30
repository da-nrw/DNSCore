begin;
# Config Script for the public mets feature behaviour

update users set use_public_mets=FALSE where use_public_mets is null;

# Config Script for setting use_Public_mets feature for DE-5, DE-6 and DE-61;

update users set use_public_mets=TRUE where short_name IN ('DE-5','DE-6','DE-61');

commit;