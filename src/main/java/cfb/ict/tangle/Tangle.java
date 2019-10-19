package cfb.ict.tangle;

import cfb.ict.Properties;
import cfb.ict.cryptography.Hash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Tangle {

    final Properties properties;

    public final Map<Hash, Vertex> verticesByHash = new ConcurrentHashMap<Hash, Vertex>();
    final Map<Hash, Set<Vertex>> verticesByAddress = new ConcurrentHashMap<Hash, Set<Vertex>>();
    final Map<Hash, Set<Vertex>> verticesByTag = new ConcurrentHashMap<Hash, Set<Vertex>>();

    final Random random = new Random();

    public Tangle(final Properties properties) {

        this.properties = properties;

        verticesByHash.put(Hash.NULL, new Vertex(Hash.NULL));
    }

    public Vertex get(final Hash hash) {

        return verticesByHash.get(hash);
    }

    public boolean put(final Transaction transaction) {

        Vertex vertex = verticesByHash.get(transaction.hash);
        if (vertex == null) {

            vertex = new Vertex(transaction.hash);
            verticesByHash.put(transaction.hash, vertex);
        }

        if (vertex.transaction == null) {

            vertex.transaction = transaction;

            vertex.trunkVertex = verticesByHash.get(transaction.trunkTransactionHash);
            if (vertex.trunkVertex == null) {

                vertex.trunkVertex = new Vertex(transaction.trunkTransactionHash);
                verticesByHash.put(transaction.trunkTransactionHash, vertex.trunkVertex);
            }
            vertex.trunkVertex.addReferrer(vertex);

            if (transaction.branchTransactionHash.equals(transaction.trunkTransactionHash)) {

                vertex.branchVertex = vertex.trunkVertex;

            } else {

                vertex.branchVertex = verticesByHash.get(transaction.branchTransactionHash);
                if (vertex.branchVertex == null) {

                    vertex.branchVertex = new Vertex(transaction.branchTransactionHash);
                    verticesByHash.put(transaction.branchTransactionHash, vertex.branchVertex);
                }
                vertex.branchVertex.addReferrer(vertex);
            }

            if (!transaction.address.equals(Hash.NULL)) {

                Set<Vertex> vertices = verticesByAddress.get(transaction.address);
                if (vertices == null) {

                    vertices = Collections.synchronizedSet(new HashSet<Vertex>());
                    verticesByAddress.put(transaction.address, vertices);
                }
                vertices.add(vertex);
            }

            if (!transaction.tag.equals(Hash.NULL)) {

                Set<Vertex> vertices = verticesByTag.get(transaction.tag);
                if (vertices == null) {

                    vertices = Collections.synchronizedSet(new HashSet<Vertex>());
                    verticesByTag.put(transaction.tag, vertices);
                }
                vertices.add(vertex);
            }

            pruneIfNecessary();

            return true;

        } else {

            pruneIfNecessary();

            return false;
        }
    }

    public boolean remove(final Hash hash) {

        final Vertex vertex = verticesByHash.get(hash);
        if (vertex == null || vertex.transaction == null) {

            return false;

        } else {

            if (!vertex.transaction.address.equals(Hash.NULL)) {

                final Set<Vertex> vertices = verticesByAddress.get(vertex.transaction.address);
                vertices.remove(vertex);
                if (vertices.isEmpty()) {

                    verticesByAddress.remove(vertex.transaction.address);
                }
            }

            if (!vertex.transaction.tag.equals(Hash.NULL)) {

                final Set<Vertex> vertices = verticesByTag.get(vertex.transaction.tag);
                vertices.remove(vertex);
                if (vertices.isEmpty()) {

                    verticesByTag.remove(vertex.transaction.tag);
                }
            }

            vertex.trunkVertex.referrers.remove(vertex);
            if (vertex.trunkVertex.referrers.isEmpty() && vertex.trunkVertex.transaction == null) {

                verticesByHash.remove(vertex.trunkVertex.hash);
            }

            if (vertex.trunkVertex != vertex.branchVertex) {

                vertex.branchVertex.referrers.remove(vertex);
                if (vertex.branchVertex.referrers.isEmpty() && vertex.branchVertex.transaction == null) {

                    verticesByHash.remove(vertex.branchVertex.hash);
                }
            }

            vertex.transaction = null;

            return true;
        }
    }

    public void copyTransactionToRequestHash(final byte[] trits, final int offset) {

        System.arraycopy(Hash.NULL.trits, 0, trits, offset, Hash.LENGTH); // TODO: Change!
    }

    public Hash bestReferrerHash(final Vertex vertex) {

        try {

            return vertex.referrers.toArray(new Vertex[0])[random.nextInt(vertex.referrers.size())].hash;

        } catch (final Exception e) {

            return null;
        }
    }

    private void pruneIfNecessary() {

        if (verticesByHash.size() > properties.tangleCapacity) {

            for (int i = 0; i < properties.tanglePruningScale; i++) {

                // TODO: Implement!
            }
        }
    }
}
