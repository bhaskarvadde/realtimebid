export default function AuthAside({ heading, text }) {
  return (
    <aside className="auth-aside">
      <span className="eyebrow">Realtime Auctions</span>
      <h2>{heading}</h2>
      <p>{text}</p>
      <ul className="auth-points">
        <li>Live bid updates, no refreshing</li>
        <li>Fair-play anti-sniping protection</li>
        <li>Secure, token-based sign-in</li>
      </ul>
    </aside>
  );
}