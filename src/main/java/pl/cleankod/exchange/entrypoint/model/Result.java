package pl.cleankod.exchange.entrypoint.model;

public record Result<T, E>(T value, E error) {
		
		public static <T, E> Result<T, E> success(T value) {
				return new Result<>(value, null);
		}
		
		public static <T, E> Result<T, E> error(E error) {
				return new Result<>(null, error);
		}
		
		public boolean isSuccess() {
				return value != null;
		}
}