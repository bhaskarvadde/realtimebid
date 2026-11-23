const pad = (n) => String(n).padStart(2, "0");

export function money(value) {
  if (value === null || value === undefined || value === "") return "—";
  return (
    "$" +
    Number(value).toLocaleString(undefined, {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })
  );
}

export function initials(name) {
  if (!name) return "?";
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0].toUpperCase())
    .join("");
}

export function splitDuration(ms) {
  const total = Math.max(0, Math.floor(ms / 1000));
  return {
    days: Math.floor(total / 86400),
    hours: Math.floor((total % 86400) / 3600),
    minutes: Math.floor((total % 3600) / 60),
    seconds: total % 60,
  };
}

export function formatCompact(ms) {
  const { days, hours, minutes, seconds } = splitDuration(ms);
  if (days > 0) return `${days}d ${pad(hours)}h ${pad(minutes)}m`;
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
}

export function formatDateTime(value) {
  if (!value) return "—";
  return new Date(value).toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });
}

export function formatDate(value) {
  if (!value) return "—";
  return new Date(value).toLocaleDateString(undefined, { dateStyle: "medium" });
}