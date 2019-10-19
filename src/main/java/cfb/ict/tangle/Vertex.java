package cfb.ict.tangle;

import cfb.ict.cryptography.Hash;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Vertex {

    final Hash hash;

    public Transaction transaction;
    public Vertex trunkVertex, branchVertex;
    public final Set<Vertex> referrers = Collections.synchronizedSet(new HashSet<Vertex>());

    Vertex(final Hash hash) {

        this.hash = hash;
    }

    void addReferrer(final Vertex referrer) {

        referrers.add(referrer);
    }

    @Override
    public boolean equals(final Object obj) {

        return hash.equals(((Vertex) obj).hash);
    }

    @Override
    public int hashCode() {

        return hash.hashCode();
    }
}
