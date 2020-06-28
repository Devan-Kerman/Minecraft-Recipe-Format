package net.devtech.mcrf.util;

import java.util.Objects;

import net.minecraft.util.Identifier;

public final class Id {
	public final String namespace;
	public final String val;

	public Id(String namespace, String val) {
		for (int i = 0; i < namespace.length(); i++) {
			char c = namespace.charAt(i);
			if (c != '_' && c != '-' && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '.') {
				throw new IllegalArgumentException(namespace + " is an invalid namespace!");
			}
		}
		for (int i = 0; i < val.length(); i++) {
			char c = val.charAt(i);
			if (c != '_' && c != '-' && !(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9') && c != '/' && c != '.') {
				throw new IllegalArgumentException(val + " is an invalid val!");
			}
		}

		this.namespace = namespace;
		this.val = val;
	}

	public Identifier asIdentifier() {
		return new Identifier(this.namespace, this.val);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Id)) {
			return false;
		}

		Id id = (Id) o;

		if (!Objects.equals(this.namespace, id.namespace)) {
			return false;
		}
		return Objects.equals(this.val, id.val);
	}

	@Override
	public int hashCode() {
		int result = this.namespace != null ? this.namespace.hashCode() : 0;
		result = 31 * result + (this.val != null ? this.val.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return this.namespace+':'+this.val;
	}
}
