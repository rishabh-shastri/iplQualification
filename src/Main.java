import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fixturesCsvFile = "fixtures.csv";
        String[][] fixtures = csvtoString2d(fixturesCsvFile);
        String ptsTableCsvFile = "ptsTable.csv";
        String[][] pointsTable = csvtoString2d(ptsTableCsvFile);
        String date = "06-May";
        int i=0;
        for (String[] fixture : fixtures) {
            if (fixture[0].equals(date)) {
                break;
            }
            i++;
        }
        int bestRank = willQualify (fixtures, pointsTable, "MI", i, new ArrayList<>());
        System.out.println("Best Rank:" + bestRank);
    }

    private static String[][] csvtoString2d(String fixturesCsvFile) {
        String line;
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fixturesCsvFile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][] dataArray = new String[rows.size()][];
        dataArray = rows.toArray(dataArray);
        return dataArray;
    }

    private static int willQualify(String[][] fixtures, String[][] pointsTable, String team, int index, List<String> path) {
        if (index == fixtures.length) {
            Arrays.sort(pointsTable, (a,b) -> {
                if (a[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return -1;
                }
                if (b[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return 1;
                }
                return Integer.parseInt(b[2])-Integer.parseInt(a[2]);
            });
            int i=1;
            for (String[] teamInPtsTable : pointsTable) {
                System.out.println(teamInPtsTable[0] + " " + teamInPtsTable[1] + " " + teamInPtsTable[2]);
            }
            for (String m : path) {
                System.out.print(m + " ");
            }
            System.out.println();
            for (String[] teamInPtsTable : pointsTable) {
                if (teamInPtsTable[0].equals(team)) {
                    System.out.println("Rank:" + i);
                    System.out.println("--------------------");
                    return i;
                }
                i++;
            }
        }
        String[] match = fixtures[index];
        String[][] newPointsTable = null;
        if (match[1].equals(team)) {
            newPointsTable = updatePointsTable(team, match[2], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            String matchResult = team + "win" + match[2];
            path.add(matchResult);
            int x =  willQualify(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return x;
        } else if (match[2].equals(team)) {
            newPointsTable = updatePointsTable(team, match[1], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            String matchResult = team + "win" + match[1];
            path.add(matchResult);
            int y =  willQualify(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return y;

        } else {
            newPointsTable = updatePointsTable(match[1], match[2], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            String matchResult = match[1] + "win" + match[2];
            path.add(matchResult);
            int a = willQualify(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);

            newPointsTable = updatePointsTable(match[2], match[1], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            matchResult = match[2] + "win" + match[1];
            path.add(matchResult);
            int b = willQualify(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return Math.min(a,b);
        }
    }

    private static String[][] updatePointsTable(String winner, String loser, String[][] pointsTable) {
        for (String[] team : pointsTable) {
            if(team[0].equals(winner)) {
                team[1] = ((Integer)(Integer.parseInt(team[1]) + 1)).toString();
                team[2] = ((Integer)(Integer.parseInt(team[2]) + 1)).toString();
            }
            else if(team[0].equals(loser)) {
                team[1] = ((Integer)(Integer.parseInt(team[1]) + 1)).toString();
            }
        }
        return pointsTable;
    }
}