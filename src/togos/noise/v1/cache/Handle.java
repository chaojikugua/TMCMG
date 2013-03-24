package togos.noise.v1.cache;

import togos.noise.v1.func.Function;

class Handle<K,V> {
	public final K key;
	volatile V value = null;
	
	public Handle( K key ) {
		assert key != null;
		this.key = key;
	}
	
	synchronized V getValue( Function<K,V> generator ) {
		if( value == null ) {
			value = generator.apply(key);
			assert value != null;
		}
		return value;
	}
	
	@Override
	public boolean equals( Object other ) {
		return other == this || (other instanceof Handle && key.equals(((Handle<?,?>)other).key));
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
}