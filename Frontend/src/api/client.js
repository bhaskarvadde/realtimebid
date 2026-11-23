// const BASE_URL = "http://localhost:8080/api/v1";

// export async function apiFetch(path, options = {}) {
//   const headers = { "Content-Type": "application/json", ...(options.headers || {}) };

//   const token = localStorage.getItem("token");
//   if (token) {
//     headers["Authorization"] = `Bearer ${token}`;
//   }

//   const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });

//   const text = await res.text();
//   let data = null;
//   try {
//     data = text ? JSON.parse(text) : null;
//   } catch {
//     data = text; // register returns plain text, not JSON
//   }

//   if (res.status === 403) {
//     throw new Error("Session expired or not allowed. Please log in again.");
//   }
//   if (!res.ok) {
//     throw new Error((data && data.message) || `Request failed (${res.status})`);
//   }
//   return data;
// }



const API_BASE_URL = "http://localhost:8080/api/v1";

export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem("token");

  const headers = {
    ...(options.headers || {}),
  };

  // Set JSON content type when sending a JSON body.
  if (
    options.body &&
    !(options.body instanceof FormData) &&
    !headers["Content-Type"]
  ) {
    headers["Content-Type"] = "application/json";
  }

  // Attach JWT when available.
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const url = `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;

  const response = await fetch(url, {
    ...options,
    headers,
  });

  // Handle empty responses.
  if (response.status === 204) {
    return null;
  }

  const responseText = await response.text();

  let data = null;

  if (responseText) {
    try {
      data = JSON.parse(responseText);
    } catch {
      data = responseText;
    }
  }

  if (!response.ok) {
    const message =
      typeof data === "object" && data !== null
        ? data.message || data.error || data.detail
        : data;

    throw new Error(
      message || `Request failed with status ${response.status}`
    );
  }

  return data;
}
