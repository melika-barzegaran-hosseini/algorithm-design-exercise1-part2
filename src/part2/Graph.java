package part2;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Represents a graph.
 * It is used to obtain the biggest connected component in a peer-to-peer network.
 *
 * @author melika barzegaran hosseini
 */
public class Graph
{
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    public Graph(String path)
    {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();

        fillVerticesAndEdges(read(path));
    }

    private int[][] read(String path)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

            String line;

            if((line = reader.readLine()) == null)
            {
                System.err.println("error: testcase file format is violated. first line is null.");
                System.exit(1);
            }

            int numberOfVertices = Integer.parseInt(line);
            int[][] connections = new int[numberOfVertices][numberOfVertices];

            for(int i = 0; i < numberOfVertices; i++)
            {
                if((line = reader.readLine()) == null)
                {
                    System.err.println("testcase file format is violated. line " + i + "th is null.");
                    System.exit(1);
                }

                String[] tokens = line.split(" ");

                if(tokens.length != numberOfVertices)
                {
                    System.err.println("testcase file format is violated. line " + i + "th does not contain all " +
                            "connections.");
                    System.exit(1);
                }

                for(int j = 0; j < numberOfVertices; j++)
                {
                    connections[i][j] = Integer.parseInt(tokens[j]);
                }
            }

            System.out.println("input:");
            for(int i = 0; i < numberOfVertices; i++)
            {
                for(int j = 0; j < numberOfVertices; j++)
                {
                    System.out.print(connections[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();

            return connections;
        }
        catch (FileNotFoundException e)
        {
            System.err.println("error: testcase file was not found.");
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("error: an error occurred while reading from the testcase file.");
            System.exit(1);
        }
        catch (NumberFormatException e)
        {
            System.err.println("error: testcase file format is violated.\n first line should represent the number of " +
                    "vertices.\n other lines should represent the connections.");
            System.exit(1);
        }

        return null;
    }

    private void fillVerticesAndEdges(int[][] connections)
    {
        for(int i = 0; i < connections.length; i++)
        {
            vertices.add(new Vertex(i));
        }
        for(int i = 0; i < connections.length; i++)
        {
            for(int j = 0; j < connections[i].length; j++)
            {
                if(i == j)
                {
                    continue;
                }

                if(connections[i][j] == 1)
                {
                    Vertex from = vertices.get(i);
                    Vertex to = vertices.get(j);
                    Edge edge = new Edge(from, to);

                    edges.add(edge);
                    from.addOutgoingEdge(edge);
                    to.addIncomingEdge(edge);
                }
            }
        }
    }

    public ArrayList<Vertex> bfs(Vertex vertex)
    {
        ArrayList<Vertex> bfs = null;

        if(!vertex.isVisited())
        {
            bfs = new ArrayList<>();
            LinkedList<Vertex> queue = new LinkedList<>();

            vertex.setVisited();
            queue.addFirst(vertex);
            bfs.add(vertex);

            while(!queue.isEmpty())
            {
                Vertex current = queue.removeLast();

                ArrayList<Vertex> neighbors = new ArrayList<>();
                for(Edge edge : current.getIncomingEdges())
                {
                    neighbors.add(edge.getStartingVertex());
                }
                for(Edge edge : current.getOutgoingEdges())
                {
                    neighbors.add(edge.getEndingVertex());
                }

                for(Vertex neighbor : neighbors)
                {
                    if(!neighbor.isVisited())
                    {
                        neighbor.setVisited();
                        queue.addFirst(neighbor);
                        bfs.add(neighbor);
                    }
                }
            }
        }

        return bfs;
    }

    public ArrayList<ArrayList<Vertex>> getConnectedComponents()
    {
        ArrayList<ArrayList<Vertex>> connectedComponents = new ArrayList<>();

        for(Vertex vertex : vertices)
        {
            ArrayList<Vertex> vertices = bfs(vertex);
            if(vertices != null)
            {
                connectedComponents.add(vertices);
            }
        }

        return connectedComponents;
    }

    public int getBiggestConnectedComponent(ArrayList<ArrayList<Vertex>> connectedComponents)
    {
        int index = 0;
        int max = connectedComponents.get(0).size();
        for(int counter = 1; counter < connectedComponents.size(); counter++)
        {
            int sizeOfCurrentComponent = connectedComponents.get(counter).size();
            if(max < sizeOfCurrentComponent)
            {
                index = counter;
                max = sizeOfCurrentComponent;
            }
        }

        return index;
    }

    @Override
    public String toString()
    {
        StringBuilder message = new StringBuilder();
        message.append("vertices: \n");
        for(Vertex vertex : vertices)
        {
            message.append(vertex).append("\n");
        }
        message.append("\nedges: \n");
        for(Edge edge : edges)
        {
            message.append(edge).append("\n");
        }
        return message.toString();
    }

    public static void test(String path)
    {
        System.out.println("=========================================================================================");
        System.out.println("testing testcase '" + path + "'...\n");

        Graph graph = new Graph(path);
        System.out.println(graph.toString());

        System.out.println("connected components: ");
        ArrayList<ArrayList<Vertex>> connectedComponents = graph.getConnectedComponents();
        System.out.println("number of connected components = " + connectedComponents.size());
        for(ArrayList<Vertex> connectedComponent : connectedComponents)
        {
            System.out.print("component " + connectedComponents.indexOf(connectedComponent) + "th: ");
            for(Vertex vertex : connectedComponent)
            {
                System.out.print(vertex.getName() + " ");
            }
            System.out.println();
        }

        System.out.println("biggest component is " + graph.getBiggestConnectedComponent(connectedComponents) + "th " +
                "component.");
        System.out.println();
    }

    public static void main(String args[])
    {
        final String TESTCASE_FOLDER = "testcases" + File.separator;
        Graph.test(TESTCASE_FOLDER + "1.txt");
        Graph.test(TESTCASE_FOLDER + "2.txt");
        Graph.test(TESTCASE_FOLDER + "3.txt");
    }
}