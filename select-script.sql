SELECT * FROM clustering.statistics as stat
INNER JOIN clustering.topterms as term ON stat.id = term.statisticId
ORDER BY timestamp DESC, numberOfDocuments DESC, numberOfOccurences DESC;