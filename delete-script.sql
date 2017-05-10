SET SQL_SAFE_UPDATES = 0;
DELETE FROM clustering.statistics;
DELETE FROM clustering.removedmicroclusters;
DELETE FROM clustering.pending;
DELETE FROM clustering.macroClusteringTasks;
DELETE FROM clustering.outlierMicroClusters;
SET SQL_SAFE_UPDATES = 1;