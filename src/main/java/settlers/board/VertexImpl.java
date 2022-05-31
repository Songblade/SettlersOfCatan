package settlers.board;

import settlers.Player;
import settlers.card.Resource;

import java.util.Arrays;

public class VertexImpl implements Vertex {

    private final Vertex[] adjVertices; // adjacent vertices
    private final Edge[] adjEdges; // adjacent edges
    private Resource port; // if you would get a port by building a settlement here, this is what you would get
    private Player player; // if a player built a settlement or city here, this is that player
    private boolean isCity; // if this is a city or not, starts false by default
    private final int id;

    public VertexImpl() {
        adjVertices = new Vertex[3];
        adjEdges = new Edge[3];
        id = (int) (Math.random() * Integer.MAX_VALUE);
    }

    /**
     * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
     */
    @Override
    public Vertex[] getAdjacentVertices() {
        return Arrays.copyOf(adjVertices, adjVertices.length);
    }

    /**
     * @param vertex   being set adjacent to the Vertex
     * @param position from 0 to 3, where the vertex is set, where 0 is the upper left, increasing counterclockwise
     * @throws IllegalStateException if this position's vertex is already set
     */
    @Override
    public void setVertex(Vertex vertex, int position) {
        if (position < 0 || position >= 3) {
            throw new IllegalArgumentException("position " + position + " is out of bounds");
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
        return Arrays.copyOf(adjEdges, adjEdges.length);
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
        if (adjEdges[position] != null) {
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
        return player;
    }

    /**
     * @param player that owns this Settlement
     * @throws IllegalStateException if a different player already has this city
     */
    @Override
    public void setPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is null");
        }
        if (this.player != null) {
            throw new IllegalStateException("This vertex is already occupied");
        }
        this.player = player;
    }

    /**
     * @return true if this is a City, false otherwise
     */
    @Override
    public boolean isCity() {
        return isCity;
    }

    /**
     * Turns this settlement into a city
     * @throws IllegalStateException if this is not a Settlement (either empty or city)
     */
    @Override
    public void makeCity() {
        if (player == null) {
            throw new IllegalStateException("There is no settlement here");
        }
        if (isCity) {
            throw new IllegalStateException("There is already a city here");
        }
        isCity = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //VertexImpl vertex = (VertexImpl) o;
        return this.hashCode() == o.hashCode();
        //return Arrays.equals(adjEdges, vertex.adjEdges) && port == vertex.port;
    }
/*
    @Override
    public int hashCode() {
        int result = Objects.hash(port);
        result = 31 * result + Arrays.hashCode(adjEdges);
        return result;
    }
    */

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String toStringV = "V: " + id;
        if (player != null) {
            toStringV += "\n\tplayer " + player;
        }
        return toStringV;
    }
}
