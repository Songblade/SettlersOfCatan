package settlers.board;

import settlers.Player;
import settlers.card.Resource;

import java.util.Arrays;
import java.util.Objects;

public class VertexImpl implements Vertex {

    private Vertex[] adjVertices; // adjacent vertices
    private Edge[] adjEdges; // adjacent edges
    private Resource port;

    public VertexImpl() {
        adjVertices = new Vertex[3];
        adjEdges = new Edge[3];
    }

    /**
     * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
     */
    @Override
    public Vertex[] getAdjacentVertices() {
        return adjVertices;
    }

    /**
     * @param vertex   being set adjacent to the Vertex
     * @param position from 0 to 3, where the vertex is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalStateException if this position's vertex is already set
     */
    @Override
    public void setVertex(Vertex vertex, int position) {
        if (position < 0 || position >= 3) {
            throw new IllegalArgumentException("position " + position + " is out of bounds");
        }
        if (vertex == null) {
            throw new IllegalArgumentException("vertex is null");
        }
        if (adjVertices[position] != null) {
            throw new IllegalStateException("Position " + position + " already has a vertex");
        }
        // if the slot is open to put a vertex, put it there
        adjVertices[position] = vertex;
    }

    /**
     * @return a length 3 array containing the adjacent edges, where 0 is up, increasing clockwise
     * Each edge at a position connects to the vertex of the same number
     */
    @Override
    public Edge[] getEdges() {
        return adjEdges;
    }

    /**
     * @param edge     the adjacent edge being added
     * @param position from 0 to 3, where the edge is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalStateException if this position's edge is already set
     */
    @Override
    public void setEdge(Edge edge, int position) {
        if (position < 0 || position >= 3) {
            throw new IllegalArgumentException("position " + position + " is out of bounds");
        }
        if (edge == null) {
            throw new IllegalArgumentException("edge is null");
        }
        if (adjVertices[position] != null) {
            throw new IllegalStateException("Position " + position + " already has an edge");
        }
        // if the slot is open to put a vertex, put it there
        adjEdges[position] = edge;
    }

    /**
     * @return the resource of the 2:1 port, MISC for a 3:1 port, or null if there is no port
     */
    @Override
    public Resource getPort() {
        return port;
    }

    /**
     * @param resource of the 2:1 port being added, or MISC for a 3:1 port
     * @throws IllegalArgumentException if resource is null
     * @throws IllegalStateException if there is already a port
     */

    @Override
    public void setPort(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("port resource is null");
        }
        if (port != null) {
            throw new IllegalStateException("There is already a port of resource" + resource);
        }
        port = resource;
    }

    /**
     * The player defaults as null until it is changed
     *
     * @return player that owns this Settlement or City, or null if there is no Settlement or City
     */
    @Override
    public Player getPlayer() {
        return null;
    }

    /**
     * @param player that owns this Settlement
     * @throws IllegalArgumentException if a different player already has this city
     */
    @Override
    public void setPlayer(Player player) {

    }

    /**
     * @return true if this is a City, false otherwise
     */
    @Override
    public boolean isCity() {
        return false;
    }

    /**
     * Turns this settlement into a city
     *
     * @throws IllegalArgumentException if this is already a City
     * @throws IllegalStateException    if this is not a Settlement
     */
    @Override
    public void makeCity() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexImpl vertex = (VertexImpl) o;
        return Arrays.equals(adjVertices, vertex.adjVertices) && Arrays.equals(adjEdges, vertex.adjEdges) && port == vertex.port;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(port);
        result = 31 * result + Arrays.hashCode(adjVertices);
        result = 31 * result + Arrays.hashCode(adjEdges);
        return result;
    }
}
